package edu.tj.cad.boringcrown.common.constants;

/**
 * Created by zuomlin
 */
public class Config {

    public static int HEAD_PRODUCTID = 501, TAIL_PRODUCTID = 685;

    public static int SUBJECT_JUDGE_PATTERN = 0;

    public static int ABNORMAL_JUDGE_PATTERN = 0;

    public static int SUBJECT_JUDGE_COUNT = 5;

    public static double ABNORMAL_PCT = 0.2;

    public static String EXCEL_TITLE = "2017年全国职业院校技能大赛裁判评分详情表";

    public static String AREA = "青岛";

    public static String PROJECT = "计算机辅助设计(工业产品CAD)";

    public enum SubjectJudgePattern {
        KICK_AVG_PATTERN(0, "去掉最低最高取平均"),
        AVG_PATTERN(1, "总体取平均");

        public int pattern;

        public String desc;

        SubjectJudgePattern(int pattern, String desc) {
            this.pattern = pattern;
            this.desc = desc;
        }
    }

    public enum AbnormalJudgePattern {
        AVG_PATTERN(0, "裁判长不评分,由评委修正评分"),
        ADMIN_PATTERN(1, "以裁判长评分为准");

        public int pattern;

        public String desc;

        AbnormalJudgePattern(int pattern, String desc) {
            this.pattern = pattern;
            this.desc = desc;
        }
    }

}
