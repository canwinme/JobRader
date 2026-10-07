import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * TB_INDUSTRY_VACANCY / TB_INDUSTRY_EMPLOYMENT / TB_INDUSTRY_INDEX / TB_EMPLOYMENT_GENDER
 * 4개 CSV를 Oracle에 적재. (TB_INDUSTRY_MAP은 create_tables_v2.sql에서 직접 INSERT됨)
 *
 * 사전 준비: create_tables_v2.sql + create_table_gender.sql 먼저 실행, ojdbc 드라이버 클래스패스 추가
 * 실행: javac DataLoaderV2.java && java -cp .:ojdbc11.jar DataLoader
 */
public class DataLoader {


    private static final String DB_URL = env("DB_URL", "jdbc:oracle:thin:@localhost:1521/orcl");
    private static final String DB_USER = env("DB_USERNAME", "");
    private static final String DB_PASSWORD = env("DB_PASSWORD", "");

    private static final String VACANCY_CSV = "data/TB_INDUSTRY_VACANCY.csv";
    private static final String EMPLOYMENT_CSV = "data/TB_INDUSTRY_EMPLOYMENT.csv";
    private static final String INDEX_CSV = "data/TB_INDUSTRY_INDEX.csv";
    private static final String GENDER_CSV = "data/TB_EMPLOYMENT_GENDER.csv";

    private static final String CAREER_JOB_CSV = "data/career_final.csv"; 

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            System.out.println("TB_INDUSTRY_VACANCY 적재: " + loadVacancy(conn) + "건");
            System.out.println("TB_INDUSTRY_EMPLOYMENT 적재: " + loadEmployment(conn) + "건");
            System.out.println("TB_INDUSTRY_INDEX 적재: " + loadIndex(conn) + "건");
            System.out.println("TB_EMPLOYMENT_GENDER 적재: " + loadGender(conn) + "건");
            System.out.println("TB_CAREER_JOB 적재: " + loadCareerJob(conn) + "건");

            conn.commit();
            System.out.println("커밋 완료.");
        }
    }

    // 콤마를 단순 split(",")로 나누면, 산업명에 콤마가 포함된 값(예: "광업.제조업(B,C)")이
    // CSV에서 큰따옴표로 감싸져 있어도 잘못 쪼개진다. 따옴표 안의 콤마는 구분자로 취급하지 않음.
    private static String[] parseCsvLine(String line) {
        java.util.List<String> result = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        result.add(cur.toString());
        return result.toArray(new String[0]);
    }

    private static int loadVacancy(Connection conn) throws Exception {
        String sql = "INSERT INTO TB_INDUSTRY_VACANCY "
                + "(PERIOD_YEAR, PERIOD_HALF, INDUSTRY_CD, INDUSTRY_NM, TOTAL_WORKERS, VACANCY_RATE, HIRE_RATE, SEPARATION_RATE) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int count = 0;
        try (BufferedReader reader = openCsv(VACANCY_CSV);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = parseCsvLine(line);
                ps.setInt(1, Integer.parseInt(c[0]));
                ps.setInt(2, Integer.parseInt(c[1]));
                ps.setString(3, c[2]);
                ps.setString(4, c[3]);
                ps.setLong(5, (long) Double.parseDouble(c[4]));
                setNullableDouble(ps, 6, c[5]);
                setNullableDouble(ps, 7, c[6]);
                setNullableDouble(ps, 8, c[7]);
                ps.addBatch();
                count++;
            }
            ps.executeBatch();
        }
        return count;
    }

    private static int loadEmployment(Connection conn) throws Exception {
        String sql = "INSERT INTO TB_INDUSTRY_EMPLOYMENT "
                + "(PERIOD_YEAR, PERIOD_QUARTER, INDUSTRY_CD, INDUSTRY_NM, EMPLOYED_COUNT) "
                + "VALUES (?, ?, ?, ?, ?)";
        int count = 0;
        try (BufferedReader reader = openCsv(EMPLOYMENT_CSV);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = parseCsvLine(line);
                ps.setInt(1, Integer.parseInt(c[0]));
                ps.setInt(2, Integer.parseInt(c[1]));
                ps.setString(3, c[2]);
                ps.setString(4, c[3]);
                ps.setLong(5, Long.parseLong(c[4]));
                ps.addBatch();
                count++;
            }
            ps.executeBatch();
        }
        return count;
    }

    private static int loadIndex(Connection conn) throws Exception {
        String sql = "INSERT INTO TB_INDUSTRY_INDEX "
                + "(PERIOD_YEAR, PERIOD_QUARTER, INDUSTRY_CD, INDUSTRY_NM, EMPLOYED_COUNT, GROWTH_RATE, IND_RANK) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int count = 0;
        try (BufferedReader reader = openCsv(INDEX_CSV);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = parseCsvLine(line);
                ps.setInt(1, Integer.parseInt(c[0]));
                ps.setInt(2, Integer.parseInt(c[1]));
                ps.setString(3, c[2]);
                ps.setString(4, c[3]);
                ps.setLong(5, Long.parseLong(c[4]));
                setNullableDouble(ps, 6, c[5]);
                setNullableInt(ps, 7, c.length > 6 ? c[6] : "");
                ps.addBatch();
                count++;
            }
            ps.executeBatch();
        }
        return count;
    }

   
    private static int loadGender(Connection conn) throws Exception {
        String sql = "INSERT INTO TB_EMPLOYMENT_GENDER "
                + "(PERIOD_YEAR, PERIOD_QUARTER, INDUSTRY_CD, GENDER_CD, INDUSTRY_NM, EMPLOYED_COUNT) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        int count = 0;
        try (BufferedReader reader = openCsv(GENDER_CSV);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = parseCsvLine(line);
               
                ps.setInt(1, Integer.parseInt(c[0]));
                ps.setInt(2, Integer.parseInt(c[1]));
                ps.setString(3, c[2]);
                ps.setString(4, c[4]); // GENDER_CD
                ps.setString(5, c[3]); // INDUSTRY_NM
                ps.setLong(6, Long.parseLong(c[5]));
                ps.addBatch();
                count++;
            }
            ps.executeBatch();
        }
        return count;
    }

   
    private static int loadCareerJob(Connection conn) throws Exception {
        String sql = "INSERT INTO TB_CAREER_JOB "
                + "(JOBDIC_SEQ, JOB, APTD_TYPE_CODE, PROFESSION, INDUSTRY_CD, CERTIFICATION, TRAINING, PREPARATION) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int count = 0;
        try (BufferedReader reader = openCsv(CAREER_JOB_CSV);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = parseCsvLine(line);
                ps.setString(1, c[0]);
                ps.setString(2, c[1]);
                ps.setString(3, c[2]);
                ps.setString(4, c[3]);
                ps.setString(5, c[4]);
                ps.setString(6, c.length > 5 ? c[5] : "");
                ps.setString(7, c.length > 6 ? c[6] : "");
                ps.setString(8, c.length > 7 ? c[7] : "");
                ps.addBatch();
                count++;
            }
            ps.executeBatch();
        }
        return count;
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static void setNullableDouble(PreparedStatement ps, int idx, String raw) throws SQLException {
        if (raw == null || raw.isBlank()) ps.setNull(idx, Types.NUMERIC);
        else ps.setDouble(idx, Double.parseDouble(raw));
    }

    private static void setNullableInt(PreparedStatement ps, int idx, String raw) throws SQLException {
        if (raw == null || raw.isBlank()) ps.setNull(idx, Types.NUMERIC);
        else ps.setInt(idx, (int) Double.parseDouble(raw));
    }

    // pandas to_csv(encoding="utf-8-sig") 결과의 BOM 제거 후 읽기
    private static BufferedReader openCsv(String path) throws Exception {
        BufferedReader reader = new BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(path), java.nio.charset.StandardCharsets.UTF_8)
        );
        reader.mark(1);
        int firstChar = reader.read();
        if (firstChar != 0xFEFF) reader.reset();
        return reader;
    }
}