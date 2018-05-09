package edu.tj.cad.boringcrown.web;

import edu.tj.cad.boringcrown.common.RestResponse;
import edu.tj.cad.boringcrown.common.constants.Config;
import edu.tj.cad.boringcrown.domain.dto.AbnormalDto;
import edu.tj.cad.boringcrown.domain.dto.MonitorInitDto;
import edu.tj.cad.boringcrown.facade.AdminFacade;
import edu.tj.cad.boringcrown.facade.MonitorFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zuomlin
 */
@Slf4j
@RestController
public class AdminController {

    @Resource
    private AdminFacade adminFacade;

    @Resource
    private MonitorFacade monitorFacade;

    @RequestMapping(value = "/admin/params")
    public RestResponse<Boolean> setParams(@RequestParam(required = false) Integer subjectJudgePattern,
                                           @RequestParam(required = false) Integer abnormalJudgePattern,
                                           @RequestParam(required = false) Integer subjectJudgeCount,
                                           @RequestParam(required = false) Integer headProductId,
                                           @RequestParam(required = false) Integer tailProductId,
                                           @RequestParam(required = false) Double abnormalPct,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(required = false) String project,
                                           @RequestParam(required = false) String area) {
        if (subjectJudgePattern != null) {
            Config.SUBJECT_JUDGE_PATTERN = subjectJudgePattern;
        }
        if (abnormalJudgePattern != null) {
            Config.ABNORMAL_JUDGE_PATTERN = abnormalJudgePattern;
        }
        if (subjectJudgeCount != null) {
            Config.SUBJECT_JUDGE_COUNT = subjectJudgeCount;
        }
        if (headProductId != null) {
            Config.HEAD_PRODUCTID = headProductId;
        }
        if (tailProductId != null)  {
            Config.TAIL_PRODUCTID = tailProductId;
        }
        if (abnormalPct != null) {
            Config.ABNORMAL_PCT = abnormalPct;
        }
        if (StringUtils.isNotBlank(title)) {
            Config.EXCEL_TITLE = title;
        }
        if (StringUtils.isNotBlank(project)) {
            Config.PROJECT = project;
        }
        if (StringUtils.isNotBlank(area)) {
            Config.AREA = area;
        }
        return new RestResponse(true);
    }

    /**
     * 客观题异常查询
     * 所有评委评分完成后开启
     *
     * @return
     */
    @GetMapping(value = "/admin/abnormals")
    public RestResponse<List<AbnormalDto>> getAbnormalScores() {
        try {
            List<AbnormalDto> abnormalDtoList = adminFacade.getAbnormalDtoItems();
            return new RestResponse(abnormalDtoList);
        } catch (Exception e) {
            log.error("异常检查failed", e);
            return new RestResponse(false, e.getMessage(), null);
        }
    }

    /**
     * 异常项重评分
     *
     * @param abnormalDtos
     * @return
     */
    @PostMapping(value = "/admin/abnormals")
    public RestResponse<Boolean> setAbnormalScore(@RequestBody List<AbnormalDto> abnormalDtos) {
        try {
            for (AbnormalDto abnormalDto : abnormalDtos) {
                adminFacade.setAbnormalScore(abnormalDto);
            }
            return new RestResponse(true);
        } catch (Exception e) {
            log.error("异常重评分failed", e);
            return new RestResponse(false, e.getMessage(), false);

        }
    }

    /**
     * 导入评委
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/admin/jurys")
    public RestResponse<Boolean> importJurys(@RequestParam("juryFile") MultipartFile file) {
        if (file.isEmpty()) {
            return new RestResponse(false, "文件为空", false);
        }
        try {
            return new RestResponse(adminFacade.importJurys(file));
        } catch (Exception e) {
            log.error("评委文件导入failed", e);
            return new RestResponse(false, e.getMessage(), false);
        }
    }

    /**
     * 导入评分标准
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/admin/criterias")
    public RestResponse<Boolean> importCriterias(@RequestParam("criteriaFile") MultipartFile file) {
        if (file.isEmpty()) {
            return new RestResponse(false, "文件为空", false);
        }
        try {
            return new RestResponse(adminFacade.importCriterias(file));
        } catch (Exception e) {
            log.error("评分标准文件导入failed", e);
            return new RestResponse(false, e.getMessage(), false);
        }
    }

    @RequestMapping(value = "/admin/reports/jury")
    public RestResponse<Boolean> genJuryReports() {
        try {
            return new RestResponse(adminFacade.genJuryReports());
        } catch (Exception e) {
            log.error("评委表格生成failed", e);
            return new RestResponse(false, e.getMessage(), false);
        }
    }

    @RequestMapping(value = "/admin/reports/product")
    public RestResponse<Boolean> genProductReports() {
        try {
            return new RestResponse(adminFacade.genProductReports());
        } catch (Exception e) {
            log.error("作品表格生成failed", e);
            return new RestResponse(false, e.getMessage(), false);
        }
    }

    @GetMapping(value = "/admin/monitor")
    public RestResponse<MonitorInitDto> initMonitor() {
        try {
            MonitorInitDto result = monitorFacade.getMonitorInitDto();
            return new RestResponse(result);
        } catch (Exception e) {
            log.error("监控初始化failed", e);
            return new RestResponse(false, e.getMessage(), false);
        }
    }

}
