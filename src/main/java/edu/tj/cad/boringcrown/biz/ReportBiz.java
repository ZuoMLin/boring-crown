package edu.tj.cad.boringcrown.biz;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.tj.cad.boringcrown.bo.CriteriaScoreBo;
import edu.tj.cad.boringcrown.common.constants.Config;
import edu.tj.cad.boringcrown.domain.dto.ScoreDto;
import edu.tj.cad.boringcrown.domain.entity.AbnormalEntity;
import edu.tj.cad.boringcrown.domain.entity.CriteriaEntity;
import edu.tj.cad.boringcrown.domain.entity.UserEntity;
import jxl.Range;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by zuomlin
 */
@Slf4j
@Component
public class ReportBiz {

    @Resource
    private UserBiz userBiz;

    @Resource
    private ScoreBiz scoreBiz;

    @Resource
    private AbnormalBiz abnormalBiz;

    public boolean genJuryReports() throws Exception {
        File root = new File(System.getProperty("user.home")+"/评分系统表格汇总");
        if (!root.exists()) {
            root.mkdir();
        }
        File dir = new File(System.getProperty("user.home")+"/评分系统表格汇总/评委评分详情");
        if (!dir.exists()) {
            dir.mkdir();
        }

        // 查询所有的评委，依次生成评委评分详情
        List<UserEntity> userEntityList = userBiz.getJurysFinished();
        for (UserEntity jury : userEntityList) {
            String filePath = System.getProperty("user.home")+"/评分系统表格汇总/评委评分详情/评委" + jury.getUsername() + "评分详情.xls";
            File file = new File(filePath);

            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("第一页", 0);

            //设置cell样式
            WritableCellFormat textFormat = getTextFormat();
            WritableCellFormat dps2NumberFormat = getDps2NumberFormat();
            WritableCellFormat dps0NumberFormat = getDps0NumberFormat();
            int juryId = jury.getUserId();
            List<CriteriaEntity> criteriaList = scoreBiz.getCriteriasByJuryId(juryId);

            for (int j = 0; j < criteriaList.size()+1; j++)
                sheet.setColumnView(j, 11);
            sheet.setRowView(6, 600);
            sheet.setRowView(7, 800);

            // 表格表头
            Range titleRg = sheet.mergeCells(0, 0, Math.max(criteriaList.size(), 7), 3);
            sheet.addCell(new Label(titleRg.getTopLeft().getColumn(), titleRg.getTopLeft().getRow(), Config.EXCEL_TITLE, textFormat));
            sheet.addCell(new Label(0, 4, "赛区", textFormat));
            //待填
            Range saiquBlkRg = sheet.mergeCells(1, 4, Math.max(criteriaList.size(), 7), 4);
            sheet.addCell(new Label(saiquBlkRg.getTopLeft().getColumn(), saiquBlkRg.getTopLeft().getRow(), Config.AREA, textFormat));

            sheet.addCell(new Label(0, 5, "赛项名称", textFormat));
            //待填
            Range saixiangBlkRg = sheet.mergeCells(1, 5, 3, 5);
            sheet.addCell(new Label(saixiangBlkRg.getTopLeft().getColumn(), saixiangBlkRg.getTopLeft().getRow(), Config.PROJECT, textFormat));

            sheet.addCell(new Label(4, 5, "竞赛模块", textFormat));
            //待填
            Range jinsaiBlkRg = sheet.mergeCells(5, 5, Math.max(criteriaList.size(), 7), 5);
            sheet.addCell(new Label(jinsaiBlkRg.getTopLeft().getColumn(), jinsaiBlkRg.getTopLeft().getRow(), "", textFormat));

            sheet.addCell(new Label(0, 6, "组别（批次）", textFormat));
            //待填
            Range zubieBlkRg = sheet.mergeCells(1, 6, Math.max(criteriaList.size(), 7), 6);
            sheet.addCell(new Label(zubieBlkRg.getTopLeft().getColumn(), zubieBlkRg.getTopLeft().getRow(), "", textFormat));

            Range zuopinRg = sheet.mergeCells(0, 7, 0, 8);
            sheet.addCell(new Label(zuopinRg.getTopLeft().getColumn(), zuopinRg.getTopLeft().getRow(), "赛位号或作品号", textFormat));

            for (int j = 0; j < criteriaList.size(); j++) {
                Range blkRg = sheet.mergeCells(j+1, 7, j+1, 8);
                sheet.addCell(new Label(blkRg.getTopLeft().getColumn(), blkRg.getTopLeft().getRow(), criteriaList.get(j).getContent1()+"/"+criteriaList.get(j).getContent2()+"/"+criteriaList.get(j).getContent3()+"/"+criteriaList.get(j).getPartGrade(), textFormat));
            }
            int offset = 9;
            for (int k = Config.HEAD_PRODUCTID; k <= Config.TAIL_PRODUCTID; ++k) {
                List<ScoreDto> scoreDtoList = scoreBiz.getScoresByJuryIdAndProductId(juryId, k);
                int row = offset + k - Config.HEAD_PRODUCTID;
                sheet.addCell(new jxl.write.Number(0, row, k, dps0NumberFormat));

                for (int q = 0; q < scoreDtoList.size(); ++q) {
                    ScoreDto scoreDtoTemp = scoreDtoList.get(q);
                    AbnormalEntity abnormal = abnormalBiz.getAbnormalByProductIdAndCriteriaId(scoreDtoTemp.getProductId(), scoreDtoTemp.getCriteriaId());
                    if (abnormal == null) {
                        sheet.addCell(new Number(q+1, row, scoreDtoTemp.getScore(), dps2NumberFormat));
                    } else {
                        if (abnormal.getJuryIdA() == juryId) {
                            sheet.addCell(new Number(q+1, row, abnormal.getScoreA(), dps2NumberFormat));
                        } else if (abnormal.getJuryIdB() == juryId) {
                            sheet.addCell(new Number(q+1, row, abnormal.getScoreB(), dps2NumberFormat));
                        }
                    }
                }
            }
            int totalOffset = offset + Config.TAIL_PRODUCTID - Config.HEAD_PRODUCTID;

            Range timeRg = sheet.mergeCells(0, totalOffset + 1, Math.max(criteriaList.size(), 7), totalOffset + 3);
            sheet.addCell(new Label(timeRg.getTopLeft().getColumn(), timeRg.getTopLeft().getRow(), "裁判签名:                                                               日期：", textFormat));

            workbook.write();
            workbook.close();

        }
        return true;

    }

    public boolean genProductReports() throws Exception {
        File root = new File(System.getProperty("user.home")+"/评分系统表格汇总");
        if (!root.exists()) {
            root.mkdir();
        }
        File dir = new File(System.getProperty("user.home")+"/评分系统表格汇总/作品最终成绩");
        if (!dir.exists()) {
            dir.mkdir();
        }

        // 记录每个作品每部分分数
        ArrayList<ArrayList<Double>> scoreInfoList = new ArrayList<>();

        // 生成每个作品的成绩单
        for (int productId = Config.HEAD_PRODUCTID; productId <= Config.TAIL_PRODUCTID; ++productId) {

            String filePath = System.getProperty("user.home")+"/评分系统表格汇总/作品最终成绩/作品" + productId + "成绩单.xls";
            File file = new File(filePath);
            WritableWorkbook book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("第一页", 0);
            WritableCellFormat textFormat = getTextFormat();
            WritableCellFormat dps2NumberFormat = getDps2NumberFormat();
            //设置cell样式
            for (int i = 0; i < 8; i++)
                sheet.setColumnView(i, 11);

            //表头表单
            Range titleRg = sheet.mergeCells(0, 0, 7, 3);
            sheet.addCell(new Label(titleRg.getTopLeft().getColumn(), titleRg.getTopLeft().getRow(), Config.EXCEL_TITLE, textFormat));
            Range saiquRg = sheet.mergeCells(0, 4, 1, 4);
            sheet.addCell(new Label(saiquRg.getTopLeft().getColumn(), saiquRg.getTopLeft().getRow(), "赛区", textFormat));
            //待填
            Range saiquBlkRg = sheet.mergeCells(2, 4, 7, 4);
            sheet.addCell(new Label(saiquBlkRg.getTopLeft().getColumn(), saiquBlkRg.getTopLeft().getRow(), Config.AREA, textFormat));
            Range saixiangRg = sheet.mergeCells(0, 5, 1, 5);
            sheet.addCell(new Label(saixiangRg.getTopLeft().getColumn(), saixiangRg.getTopLeft().getRow(), "赛项名称", textFormat));
            //待填
            Range saixiangBlkRg = sheet.mergeCells(2, 5, 3, 5);
            sheet.addCell(new Label(saixiangBlkRg.getTopLeft().getColumn(), saixiangBlkRg.getTopLeft().getRow(), Config.PROJECT, textFormat));

            Range jinsaiRg = sheet.mergeCells(4, 5, 5, 5);
            sheet.addCell(new Label(jinsaiRg.getTopLeft().getColumn(), jinsaiRg.getTopLeft().getRow(), "竞赛模块", textFormat));
            //待填
            Range jinsaiBlkRg = sheet.mergeCells(6, 5, 7, 5);
            sheet.addCell(new Label(jinsaiBlkRg.getTopLeft().getColumn(), jinsaiBlkRg.getTopLeft().getRow(), "", textFormat));
            Range zubieRg = sheet.mergeCells(0, 6, 1, 6);
            sheet.addCell(new Label(zubieRg.getTopLeft().getColumn(), zubieRg.getTopLeft().getRow(), "组别（批次）", textFormat));
            //待填
            Range zubieBlkRg = sheet.mergeCells(2, 6, 3, 6);
            sheet.addCell(new Label(zubieBlkRg.getTopLeft().getColumn(), zubieBlkRg.getTopLeft().getRow(), "", textFormat));
            Range zuopinRg = sheet.mergeCells(4, 6, 5, 6);
            sheet.addCell(new Label(zuopinRg.getTopLeft().getColumn(), zuopinRg.getTopLeft().getRow(), "赛位号或作品号", textFormat));

            //待填
            Range zuopinBlkRg = sheet.mergeCells(6, 6, 7, 6);
            sheet.addCell(new Label(zuopinBlkRg.getTopLeft().getColumn(), zuopinBlkRg.getTopLeft().getRow(), "G"+productId, textFormat));

            Range yijiRg = sheet.mergeCells(0, 7, 1, 7);
            sheet.addCell(new Label(yijiRg.getTopLeft().getColumn(), yijiRg.getTopLeft().getRow(), "评分标准一级指标", textFormat));
            Range erjiRg = sheet.mergeCells(2, 7, 5, 7);
            sheet.addCell(new Label(erjiRg.getTopLeft().getColumn(), erjiRg.getTopLeft().getRow(), "评分标准二级指标及其分值", textFormat));
            Range defenRg = sheet.mergeCells(6, 7, 7, 7);
            sheet.addCell(new Label(defenRg.getTopLeft().getColumn(), defenRg.getTopLeft().getRow(), "得分", textFormat));

            // judge表和abnormal表查分，保留到小数点后两位
            // pattern = 0 表示去掉最高分和最低分取平均, pattern = 1 表示直接取平均
            List<CriteriaScoreBo> subjectScoreBoList =  scoreBiz.getSujectCriteriaScores(productId, Config.SUBJECT_JUDGE_COUNT, Config.SUBJECT_JUDGE_PATTERN);
            List<CriteriaScoreBo> objectScoreBoList = scoreBiz.getObjectCriteriaScores(productId);

            List<CriteriaScoreBo> abnormalScoreBoList = abnormalBiz.getCriteriaScores(productId, Config.ABNORMAL_JUDGE_PATTERN);

            TreeMap<Integer, CriteriaScoreBo> criteriaScoreBoTreeMap = Maps.newTreeMap();
            if (subjectScoreBoList != null) {
                for (CriteriaScoreBo criteriaScoreBo : subjectScoreBoList) {
                    criteriaScoreBoTreeMap.put(criteriaScoreBo.getCriteriaId(), criteriaScoreBo);
                }
            }
            if (objectScoreBoList != null) {
                for (CriteriaScoreBo criteriaScoreBo : objectScoreBoList) {
                    criteriaScoreBoTreeMap.put(criteriaScoreBo.getCriteriaId(), criteriaScoreBo);
                }
            }
            if (abnormalScoreBoList != null) {
                for (CriteriaScoreBo criteriaScoreBo : abnormalScoreBoList) {
                    criteriaScoreBoTreeMap.put(criteriaScoreBo.getCriteriaId(), criteriaScoreBo);
                }
            }

            List<Map.Entry<Integer, CriteriaScoreBo>> entryList = Lists.newArrayList(criteriaScoreBoTreeMap.entrySet());
            int offset = 8;
            int tailIndex = 0, endIndex = 0;
            String curContent1 = entryList.get(0).getValue().getContent1();
            //当前作品的部分分数列表
            ArrayList<Double> partScoreInfoList = new ArrayList<>();
            double partScoreSum = 0.0;
            double scoreSum = 0.0;
            for (int i = 0; i < entryList.size(); ++i) {
                CriteriaScoreBo criteriaScoreBo = entryList.get(i).getValue();
                if (!(criteriaScoreBo.getContent1().equals(curContent1))) {
                    Range content1Rg = sheet.mergeCells(0, offset + tailIndex, 1, offset + endIndex - 1);
                    sheet.addCell(new Label(content1Rg.getTopLeft().getColumn(), content1Rg.getTopLeft().getRow(), curContent1, textFormat));
                    curContent1 = criteriaScoreBo.getContent1();
                    tailIndex = i;
                    endIndex = i;
                    partScoreInfoList.add(partScoreSum);
                    partScoreSum = 0.0;
                }
                endIndex++;

                Range criRg = sheet.mergeCells(2, offset + i, 5, offset + i);
                String criStr = criteriaScoreBo.getContent2()+"/" + criteriaScoreBo.getContent3() +"/" + criteriaScoreBo.getPartgrade();
                sheet.addCell(new Label(criRg.getTopLeft().getColumn(), criRg.getTopLeft().getRow(), criStr, textFormat));

                Range scoreRg = sheet.mergeCells(6, offset + i, 7, offset + i);
                double score = criteriaScoreBo.getScore();
                sheet.addCell(new Number(scoreRg.getTopLeft().getColumn(), scoreRg.getTopLeft().getRow(), score, dps2NumberFormat));

                partScoreSum += score;
                scoreSum += score;

                if (i == entryList.size() - 1) {
                    Range content1Rg = sheet.mergeCells(0, offset + tailIndex, 1, offset + endIndex - 1);
                    sheet.addCell(new Label(content1Rg.getTopLeft().getColumn(), content1Rg.getTopLeft().getRow(), curContent1, textFormat));
                    curContent1 = (String) entryList.get(i).getValue().getContent1();
                    partScoreInfoList.add(partScoreSum);
                }
            }
            int hejiOffset = entryList.size() + offset;
            Range hejiLabelRg = sheet.mergeCells(0, hejiOffset, 1, hejiOffset);
            sheet.addCell(new Label(hejiLabelRg.getTopLeft().getColumn(), hejiLabelRg.getTopLeft().getRow(), "总分", textFormat));

            Range hejiRg = sheet.mergeCells(2, hejiOffset, 7, hejiOffset);
            sheet.addCell(new Number(hejiRg.getTopLeft().getColumn(), hejiRg.getTopLeft().getRow(), scoreSum, dps2NumberFormat));

            Range timeRg = sheet.mergeCells(0, hejiOffset + 1, 7, hejiOffset + 3);
            sheet.addCell(new Label(timeRg.getTopLeft().getColumn(), timeRg.getTopLeft().getRow(), "                                             日期：", textFormat));

            book.write();
            book.close();
            partScoreInfoList.add(scoreSum);
            scoreInfoList.add(partScoreInfoList);

        }
        if (genProductSumReport(scoreInfoList))
            return true;
        return false;
    }

    /**
     * 生成作品成绩汇总表
     * @param scoreInfoList
     * @return
     */
    public boolean genProductSumReport(ArrayList<ArrayList<Double>> scoreInfoList) throws Exception {
        File root = new File(System.getProperty("user.home")+"/评分系统表格汇总");
        if (!root.exists()) {
            root.mkdir();
        }
        File dir = new File(System.getProperty("user.home")+"/评分系统表格汇总/成绩汇总");
        if (!dir.exists()) {
            dir.mkdir();
        }

        //将分数打印出来
        String filePath = System.getProperty("user.home")+"/评分系统表格汇总/成绩汇总/成绩汇总.xls";
        File file = new File(filePath);

        WritableWorkbook workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("第一页", 0);

        //设置cell样式
        WritableCellFormat textFormat = getTextFormat();
        WritableCellFormat dps2NumberFormat = getDps2NumberFormat();
        WritableCellFormat dps0NumberFormat = getDps0NumberFormat();
        WritableCellFormat leftFormat = getLeftFormat();

        sheet.setColumnView(0, 16);
        for (int i = 1; i < 7; i++)
            sheet.setColumnView(i, 11);
        sheet.setRowView(4, 600);
        sheet.setRowView(5, 600);

        // 表格表头
        Range titleRg = sheet.mergeCells(0, 0, 6, 3);
        sheet.addCell(new Label(titleRg.getTopLeft().getColumn(), titleRg.getTopLeft().getRow(), Config.EXCEL_TITLE, textFormat));
        sheet.addCell(new Label(0, 4, "赛区", textFormat));
        //待填
        Range saiquBlkRg = sheet.mergeCells(1, 4, 6, 4);
        sheet.addCell(new Label(saiquBlkRg.getTopLeft().getColumn(), saiquBlkRg.getTopLeft().getRow(), Config.AREA, textFormat));

        sheet.addCell(new Label(0, 5, "赛项名称", textFormat));
        //待填
        Range saixiangBlkRg = sheet.mergeCells(1, 5, 6, 5);
        sheet.addCell(new Label(saixiangBlkRg.getTopLeft().getColumn(), saixiangBlkRg.getTopLeft().getRow(), Config.PROJECT, textFormat));

        sheet.addCell(new Label(0, 6, "赛位号或作品号", textFormat));
        //Range criRg = sheet.mergeCells(1, 6, 2, 6);
        //sheet.addCell(new Label(criRg.getTopLeft().getColumn(), criRg.getTopLeft().getRow(), "一二级指标及分值", textFormat));
        sheet.addCell(new Label(1, 6, "第一部分", textFormat));
        sheet.addCell(new Label(2, 6, "第二部分", textFormat));
        sheet.addCell(new Label(3, 6, "第三部分", textFormat));
        sheet.addCell(new Label(4, 6, "第四部分", textFormat));
        sheet.addCell(new Label(5, 6, "第五部分", textFormat));
        sheet.addCell(new Label(6, 6, "总分", textFormat));

        int offset = 7;

        // 表格内容

        for (int i = 0; i < scoreInfoList.size(); i++) {
            sheet.addCell(new Number(0, offset, Config.HEAD_PRODUCTID + i, dps0NumberFormat));
            int count = scoreInfoList.get(i).size();
            for (int j = 0; j < count; j++) {
                if (j == count - 1) {
                    sheet.addCell(new Number(6, offset, scoreInfoList.get(i).get(j), dps2NumberFormat));
                } else
                    sheet.addCell(new Number(j+1, offset, scoreInfoList.get(i).get(j), dps2NumberFormat));
            }
            ++offset;
        }
        Range timeRg = sheet.mergeCells(0, offset + 1, 6, offset + 3);
        sheet.addCell(new Label(timeRg.getTopLeft().getColumn(), timeRg.getTopLeft().getRow(), "            记分员签名：\n            裁判长签名：\n            监督组签名：                                                                          日期：", leftFormat));
        workbook.write();
        workbook.close();

        return true;
    }

    private WritableCellFormat getTextFormat() {
        try {
            WritableCellFormat textFormat = new WritableCellFormat();
            textFormat.setAlignment(jxl.format.Alignment.CENTRE);
            textFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            textFormat.setWrap(true);
            return textFormat;
        } catch (WriteException e) {
            log.error("initialize excel cell format failure", e);
            return null;
        }
    }

    private WritableCellFormat getLeftFormat() {
        try {
            WritableCellFormat leftFormat = new WritableCellFormat();
            leftFormat.setAlignment(jxl.format.Alignment.LEFT);
            leftFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            leftFormat.setWrap(true);
            return leftFormat;
        } catch (WriteException e) {
            log.error("initialize excel cell format failure", e);
            return null;
        }
    }

    private WritableCellFormat getDps2NumberFormat() {
        try {
            NumberFormat dps2Format = new NumberFormat("0.00");
            WritableCellFormat dps2NumberFormat = new WritableCellFormat(dps2Format);
            dps2NumberFormat.setAlignment(jxl.format.Alignment.CENTRE);
            dps2NumberFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            dps2NumberFormat.setWrap(true);
            return dps2NumberFormat;
        } catch (WriteException e) {
            log.error("initialize excel cell format failure", e);
            return null;
        }
    }

    private WritableCellFormat getDps0NumberFormat() {
        try {
            NumberFormat dps0Format = new NumberFormat("0");
            WritableCellFormat dps0NumberFormat = new WritableCellFormat(dps0Format);
            dps0NumberFormat.setAlignment(jxl.format.Alignment.CENTRE);
            dps0NumberFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            dps0NumberFormat.setWrap(true);
            return dps0NumberFormat;
        } catch (WriteException e) {
            log.error("initialize excel cell format failure", e);
            return null;
        }
    }
}
