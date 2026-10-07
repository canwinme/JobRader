package com.jobradar.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REQ-004: KOSIS 산업 세분류(KSIC 대분류, A~U)를 6개 그룹으로 재집계한다.
 * (전산업 총계는 원본에서 별도 제공되므로 여기서는 5개 그룹 합산만 다룸)
 *
 * 입력 형식 (헤더 포함):
 *   산업코드,연도,분기,취업자수
 *   예) B,2026,2,1200
 *
 * 출력 형식 (tab 구분):
 *   그룹코드_연도_분기 \t 취업자수합계
 *
 * 실행: java -jar target/jobradar-hadoop-1.0.jar com.jobradar.hadoop.EmploymentAggregationJob input/employment_raw.csv output/employment
 */
public class EmploymentAggregationJob {

    private static final Map<String, String> KSIC_TO_GROUP = new HashMap<>();
    static {
        KSIC_TO_GROUP.put("B", "BC");
        KSIC_TO_GROUP.put("C", "BC");
        KSIC_TO_GROUP.put("F", "F");
        KSIC_TO_GROUP.put("G", "GI");
        KSIC_TO_GROUP.put("I", "GI");
        for (String c : new String[]{"E", "L", "M", "N", "O", "P", "Q", "R", "S"}) {
            KSIC_TO_GROUP.put(c, "ELS");
        }
        for (String c : new String[]{"D", "H", "J", "K"}) {
            KSIC_TO_GROUP.put(c, "DHJK");
        }

        KSIC_TO_GROUP.put("ALL", "ALL");
        KSIC_TO_GROUP.put("BC", "BC");
        KSIC_TO_GROUP.put("GI", "GI");
        KSIC_TO_GROUP.put("ELS", "ELS");
        KSIC_TO_GROUP.put("DHJK", "DHJK");
    }

    public static class EmploymentMapper extends Mapper<Object, Text, Text, LongWritable> {

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (line.startsWith("산업코드") || line.trim().isEmpty()) return;

            String[] f = line.split(",");
            if (f.length < 4) return;

            String rawCd = f[0].trim();
            String group = KSIC_TO_GROUP.get(rawCd);
            if (group == null) return;

            try {
                String year = f[1];
                String quarter = f[2];
                long count = Long.parseLong(f[3].trim());
                context.write(new Text(group + "_" + year + "_" + quarter), new LongWritable(count));
            } catch (NumberFormatException e) {
                // 결측 행 skip
            }
        }
    }

    public static class EmploymentReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long sum = 0;
            for (LongWritable v : values) sum += v.get();
            context.write(key, new LongWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("사용법: EmploymentAggregationJob <입력파일> <출력디렉터리>");
            System.exit(1);
        }
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Employment 27-to-6 Category Aggregation");
        job.setJarByClass(EmploymentAggregationJob.class);
        job.setMapperClass(EmploymentMapper.class);
        job.setReducerClass(EmploymentReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
