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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * REQ-006: 산업별 시계열 값(취업자수 또는 빈일자리율 등)을 받아
 * 전기 대비 증감률을 계산한다. VacancyAggregationJob·EmploymentAggregationJob의
 * 출력을 정리해 아래 입력 형식으로 만든 뒤 이 Job의 입력으로 사용한다.
 *
 * 입력 형식 (헤더 포함):
 *   산업코드,연도,기간,값
 *   예) BC,2025,2,1200000
 *
 * 출력 형식 (tab 구분):
 *   산업코드 \t 연도,기간,값,증감률(%)
 *   (해당 산업의 가장 이른 시점은 이전 기간이 없어 증감률이 빈 값으로 나온다)
 *
 * 실행: java -jar target/jobradar-hadoop-1.0.jar com.jobradar.hadoop.GrowthRateJob input/index_raw.csv output/growth
 */
public class GrowthRateJob {

    public static class GrowthMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (line.startsWith("산업코드") || line.trim().isEmpty()) return;

            String[] f = line.split(",");
            if (f.length < 4) return;

            String industryCd = f[0];
            String rest = f[1] + "," + f[2] + "," + f[3];
            context.write(new Text(industryCd), new Text(rest));
        }
    }

    public static class GrowthReducer extends Reducer<Text, Text, Text, Text> {

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            List<int[]> periods = new ArrayList<>();
            List<Double> amounts = new ArrayList<>();

            for (Text v : values) {
                String[] p = v.toString().split(",");
                periods.add(new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1])});
                amounts.add(Double.parseDouble(p[2]));
            }

            Integer[] idx = new Integer[periods.size()];
            for (int i = 0; i < idx.length; i++) idx[i] = i;
            java.util.Arrays.sort(idx, Comparator
                    .<Integer>comparingInt(i -> periods.get(i)[0])
                    .thenComparingInt(i -> periods.get(i)[1]));

            Double prevAmount = null;
            for (int i : idx) {
                int year = periods.get(i)[0];
                int term = periods.get(i)[1];
                double amount = amounts.get(i);

                String growth = "";
                if (prevAmount != null && prevAmount != 0) {
                    growth = String.format("%.2f", (amount - prevAmount) / prevAmount * 100);
                }
                context.write(key, new Text(year + "," + term + "," + amount + "," + growth));
                prevAmount = amount;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("사용법: GrowthRateJob <입력파일> <출력디렉터리>");
            System.exit(1);
        }
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Industry Growth Rate Calculation");
        job.setJarByClass(GrowthRateJob.class);
        job.setMapperClass(GrowthMapper.class);
        job.setReducerClass(GrowthReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
