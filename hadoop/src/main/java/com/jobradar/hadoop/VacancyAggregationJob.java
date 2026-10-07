package com.jobradar.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * REQ-005: 지역별 원자료(전체종사자·빈일자리율·입직률·이직률)를
 * 전체종사자 수 기준 가중평균으로 전국 단위 집계한다.
 *
 * 입력 형식 (전처리 완료된 CSV, 헤더 포함):
 *   지역,산업코드,연도,반기,전체종사자,빈일자리율,입직률,이직률
 *   예) 서울특별시종로구,BC,2025,2,1234,0.8,3.2,3.5
 *
 * 출력 형식 (tab 구분):
 *   산업코드_연도_반기 \t 전국전체종사자,전국빈일자리율,전국입직률,전국이직률
 *
 * 실행 (로컬 모드, Hadoop 클러스터 불필요):
 *   mvn package
 *   java -jar target/jobradar-hadoop-1.0.jar com.jobradar.hadoop.VacancyAggregationJob input/vacancy_raw.csv output/vacancy
 */
public class VacancyAggregationJob {

    public static class VacancyMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (line.startsWith("지역") || line.trim().isEmpty()) return;

            String[] f = line.split(",");
            if (f.length < 8) return;

            try {
                String industryCd = f[1];
                String year = f[2];
                String half = f[3];
                double totalWorkers = Double.parseDouble(f[4]);
                double vacancyRate = Double.parseDouble(f[5]);
                double hireRate = Double.parseDouble(f[6]);
                double sepRate = Double.parseDouble(f[7]);

                String outKey = industryCd + "_" + year + "_" + half;
                String outValue = totalWorkers + "," + (totalWorkers * vacancyRate) + ","
                        + (totalWorkers * hireRate) + "," + (totalWorkers * sepRate);
                context.write(new Text(outKey), new Text(outValue));
            } catch (NumberFormatException e) {
                // 결측치(공백) 등으로 파싱 실패한 행은 집계에서 제외
            }
        }
    }

    public static class VacancyReducer extends Reducer<Text, Text, Text, Text> {

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            double sumWorkers = 0, sumVacancy = 0, sumHire = 0, sumSep = 0;

            for (Text v : values) {
                String[] parts = v.toString().split(",");
                sumWorkers += Double.parseDouble(parts[0]);
                sumVacancy += Double.parseDouble(parts[1]);
                sumHire += Double.parseDouble(parts[2]);
                sumSep += Double.parseDouble(parts[3]);
            }
            if (sumWorkers == 0) return;

            String result = String.format("%.0f,%.2f,%.2f,%.2f",
                    sumWorkers, sumVacancy / sumWorkers, sumHire / sumWorkers, sumSep / sumWorkers);
            context.write(key, new Text(result));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("사용법: VacancyAggregationJob <입력파일> <출력디렉터리>");
            System.exit(1);
        }
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Vacancy Regional Weighted Aggregation");
        job.setJarByClass(VacancyAggregationJob.class);
        job.setMapperClass(VacancyMapper.class);
        job.setReducerClass(VacancyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
