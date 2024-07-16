package com.soop.pages.honeypot.controller;

import com.soop.pages.honeypot.model.dto.*;
import com.soop.pages.honeypot.model.service.HoneyPotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Delete;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "허니팟(소셜링) 게시글 API", description = "허니팟 관련 CRUD 작업을 수행하는 API입니다.")
@RestController
@RequestMapping("/honeypot")
public class HoneyPotController {

    private HoneyPotService honeyPotService;
    private HoneypotDTO honeypots;

    public HoneyPotController(HoneyPotService honeyPotService) {
        this.honeyPotService = honeyPotService;
    }

    //허니팟 등록
    @Operation(summary = "허니팟 등록", description = "새로운 허니팟을 등록합니다.")
    @PostMapping("/regist")
    public ResponseEntity<HoneypotDTO> registerHoneypot(@RequestBody HoneypotDTO honeypotDTO) {
        HoneypotDTO savedHoneypot = honeyPotService.saveHoneypot(honeypotDTO);
        return ResponseEntity.ok(savedHoneypot);
    }

    // 허니팟 전체 조회
    @Operation(summary = "허니팟 전체 조회", description = "모든 허니팟 정보를 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<HoneypotResponseMessage> findAllHoneypot() {

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 데이터 설정
        List<HoneypotDTO> honeypots = honeyPotService.findAllHoneypots();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("honeypots", honeypots);

        HoneypotResponseMessage responseMessage = new HoneypotResponseMessage(200, "조회 성공", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);

    }

    // 허니팟 상세페이지 조회 (허니팟 코드로 조회)
    @Operation(summary = "허니팟 상세 조회", description = "특정 허니팟의 종합된 상세 정보를 조회합니다.")
    @Parameter(name = "honeypotCode", description = "허니팟게시물 번호", in = ParameterIn.PATH)
    @GetMapping("/detail/{honeypotCode}")
    public ResponseEntity<HoneypotResponseMessage> findByHoneypotCode(@PathVariable int honeypotCode) {

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 데이터 설정
        HoneypotAndInterestAndLinkBeeUserDTO foundHoneypot = honeyPotService.findByHoneypotCode(honeypotCode);
        System.out.println(foundHoneypot);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("honeypot", foundHoneypot);

        // ResponseEntity로 응답 반환
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new HoneypotResponseMessage(200, "조회 성공", responseMap));

    }
    // 상세페이지 내용 수정용 상세페이지 조회
    @Operation(summary = "허니팟 상세 조회", description = "특정 허니팟의 수정가능한 항목만 조회합니다.")
    @Parameter(name = "honeypotCode", description = "허니팟게시물 번호", in = ParameterIn.PATH)
    @GetMapping("/detail/temporary/{honeypotCode}")
    public ResponseEntity<HoneypotResponseMessage> temporaryFindByHoneypotCode(@PathVariable int honeypotCode) {

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 데이터 설정
        HoneypotDTO temporaryFoundHoneypot = honeyPotService.temporaryFindByHoneypotCode(honeypotCode);

        System.out.println(temporaryFoundHoneypot);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("honeypot", temporaryFoundHoneypot);

        // ResponseEntity로 응답 반환
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new HoneypotResponseMessage(200, "조회 성공", responseMap));

    }

    // 상세페이지 내용 수정하기
    @Operation(summary = "허니팟 수정", description = "특정 허니팟을 수정합니다.")
    @Parameter(name = "honeypotCode", description = "허니팟게시물 번호", in = ParameterIn.PATH)
    @PutMapping("/modify/{honeypotCode}")
    public ResponseEntity<HoneypotResponseMessage> modifyHoneypot(@PathVariable int honeypotCode, @RequestBody HoneypotDTO honeypotDTO) {
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 허니팟 정보 수정
        HoneypotDTO updatedHoneypot = honeyPotService.modifyHoneypot(honeypotCode, honeypotDTO);

        // 응답 데이터 설정
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("honeypot", updatedHoneypot);

        // ResponseEntity로 응답 반환
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new HoneypotResponseMessage(200, "수정 성공", responseMap));

    }

    // 상세페이지 삭제하기
    @Operation(summary = "허니팟 삭제", description = "특정 허니팟을 삭제합니다.")
    @Parameter(name = "honeypotCode", description = "허니팟게시물 번호", in = ParameterIn.PATH)
    @DeleteMapping("/delete/{honeypotCode}")
    public ResponseEntity<HoneypotResponseMessage> deleteHoneypotByHoneypotCode(@PathVariable int honeypotCode) {
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 데이터 설정
        honeyPotService.deleteHoneypotByHoneypotCode(honeypotCode);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "허니팟 삭제 성공");

        return ResponseEntity.ok().headers(headers).body(new HoneypotResponseMessage(200, "허니팟 삭제 성공", responseMap));
    }


    // 댓글 등록
    @Operation(summary = "댓글 작성", description = "댓글을 작성합니다.")
    @PostMapping("/comment")
    public ResponseEntity<CommentAndLinkBeeUserDTO> registComment(@RequestBody CommentAndLinkBeeUserDTO newComment) {
        CommentAndLinkBeeUserDTO registComment = honeyPotService.registComment(newComment);
        return ResponseEntity.ok(registComment);
    }

    // 댓글 전체 조회
    @Operation(summary = "댓글 조회", description = "모든 댓글을 조회합니다.")
    @GetMapping("/comment")
    public ResponseEntity<HoneypotResponseMessage> findAllComment() {
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 데이터 설정
        List<CommentAndLinkBeeUserDTO> comments = honeyPotService.findAllComments();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("comments", comments);

        HoneypotResponseMessage responseMessage = new HoneypotResponseMessage(200, "조회 성공", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);
    }

    // 댓글코드로 조회(임시)
    @Operation(summary = "특정 댓글 조회", description = "특정 댓글을 조회합니다.")
    @Parameter(name = "commentCode", description = "댓글 번호", in = ParameterIn.PATH)
    @GetMapping("/comment/temporary/{commentCode}")
    public ResponseEntity<HoneypotResponseMessage> temporaryFindCommentByCommentCode(@PathVariable int commentCode) {

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 데이터 설정
        CommentDTO temporaryFoundComment = honeyPotService.temporaryFindCommentByCommentCode(commentCode);

        System.out.println(temporaryFoundComment);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("comment", temporaryFoundComment);

        // ResponseEntity로 응답 반환
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new HoneypotResponseMessage(200, "조회 성공", responseMap));

    }

    // 댓글코드로 조회
    @Operation(summary = "유저정보를 포함한 특정 댓글 조회", description = "유저정보를 포함한 특정 댓글을 조회합니다.")
    @Parameter(name = "commentCode", description = "댓글 번호", in = ParameterIn.PATH)
    @GetMapping("/comment/{commentCode}")
    public ResponseEntity<HoneypotResponseMessage> findCommentByCommentCode(@PathVariable int commentCode) {

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 데이터 설정
        CommentAndLinkBeeUserDTO foundComment = honeyPotService.findCommentByCommentCode(commentCode);

        System.out.println(foundComment);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("comment", foundComment);

        // ResponseEntity로 응답 반환
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new HoneypotResponseMessage(200, "조회 성공", responseMap));

    }

    // 댓글 수정하기
    @Operation(summary = "특정 댓글 수정", description = "특정 댓글을 수정합니다.")
    @Parameter(name = "commentCode", description = "댓글 번호", in = ParameterIn.PATH)
    @PutMapping("/comment/{commentCode}")
    public ResponseEntity<HoneypotResponseMessage> modifyComment(@PathVariable int commentCode, @RequestBody CommentDTO commentDTO) {
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 댓글 정보 수정
        CommentDTO updatedComment = honeyPotService.modifyComment(commentCode, commentDTO);

        // 응답 데이터 설정
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("comment", updatedComment);

        // ResponseEntity로 응답 반환
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new HoneypotResponseMessage(200, "수정 성공", responseMap));

    }

    // 댓글 삭제
    @Operation(summary = "특정 댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @Parameter(name = "commentCode", description = "댓글 번호", in = ParameterIn.PATH)
    @DeleteMapping("/comment/{commentCode}")
    public ResponseEntity<HoneypotResponseMessage> deleteCommentByCommentCode(@PathVariable int commentCode) {

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 데이터 설정
        honeyPotService.deleteCommentByCommentCode(commentCode);
        Map<String, Object> reponseMap = new HashMap<>();
        reponseMap.put("message", "댓글 삭제 성공");

        return ResponseEntity.ok().headers(headers).body(new HoneypotResponseMessage(200, "댓글 삭제 성공", reponseMap));
    }


    // 참가신청 등록
    @Operation(summary = "허니팟 참가 신청 등록", description = "허니팟에 참가신청을 (등록)합니다.")
    @PostMapping("/application")
    public ResponseEntity<ApplicationDTO> registComment(@RequestBody ApplicationDTO newApplication) {
        ApplicationDTO registApplication = honeyPotService.registApplication(newApplication);
        return ResponseEntity.ok(registApplication);
    }

    // 해당 허니팟의 참가신청 목록 조회
    @Operation(summary = "특정 허니팟 참가 신청 목록 조회", description = "특정 허니팟 참가신청을 목록을 조회합니다.")
    @Parameter(name = "honeypotCode", description = "허니팟 게시물 번호", in = ParameterIn.PATH)
    @GetMapping("/application/{honeypotCode}")
    public ResponseEntity<List<ApprovalStatusDTO>> findApplications(@PathVariable("honeypotCode") int honeypotCode) {
        List<ApprovalStatusDTO> applications = honeyPotService.findApplicationsByHoneypotCode(honeypotCode);
        applications.forEach(app -> System.out.println(app));
        return ResponseEntity.ok(applications);
    }

    // 해당 허니팟에 참가 신청한 사람 개별 조회(참가신청코드로 구분)
    @Operation(summary = "특정 허니팟에 참가 신청한 회원의 승인/미승인 여부 조회", description = "특정 허니팟에 참가신청을 한 특정 회원의 승인/미승인 여부를 조회합니다.")
    @Parameter(name = "honeypotCode", description = "허니팟 게시물 번호", in = ParameterIn.PATH)
    @Parameter(name = "applicationCode", description = "승인/미승인 여부 번호", in = ParameterIn.PATH)
    @GetMapping("/application/{honeypotCode}/{applicationCode}")
    public ResponseEntity<ApprovalStatusDTO> findApplicationByCodes(@PathVariable("honeypotCode") int honeypotCode,
                                                                    @PathVariable("applicationCode") int applicationCode) {
        ApprovalStatusDTO application = honeyPotService.findApplicationByHoneypotCodeAndApplicationCode(honeypotCode, applicationCode);
        if (application != null) {
            return ResponseEntity.ok(application);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 승인, 미승인 수정하기
    @Operation(summary = "특정 허니팟에 참가 신청한 회원의 승인/미승인 여부 수정", description = "특정 허니팟에 참가신청을 한 특정 회원의 승인/미승인 여부를 수정합니다.")
    @Parameter(name = "honeypotCode", description = "허니팟 게시물 번호", in = ParameterIn.PATH)
    @Parameter(name = "applicationCode", description = "승인/미승인 여부 번호", in = ParameterIn.PATH)
    @PutMapping("/application/{honeypotCode}/{applicationCode}")
    public ResponseEntity<ApprovalStatusDTO> updateApplicationData(
            @PathVariable("honeypotCode") int honeypotCode,
            @PathVariable("applicationCode") int applicationCode,
            @RequestBody ApprovalStatusDTO updatedApplication) {

        // 여기서는 ApprovalStatusDTO 객체에 있는 데이터를 기준으로 업데이트 작업을 수행할 수 있습니다.
        ApprovalStatusDTO updatedData = honeyPotService.updateApplicationData(honeypotCode, applicationCode, updatedApplication);

        if (updatedData != null) {
            return ResponseEntity.ok(updatedData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 참가 인원 수 조회하기(허니팟 리스트 + 승인상태) 아마 이게 성공하면 허니팟 전체조회는 대체 가능
    @Operation(summary = "참가인원 정보를 포함한 허니팟 리스트 조회", description = "참가신청 및 승인신청 상태의 정보를 담고 있는 허니팟 리스트 조회")
    @GetMapping("/listandapproved")
    public ResponseEntity<List<HoneypotAndApplicationAndApprovalStatusDTO>> getHoneyPotApprovedList() {
        List<HoneypotAndApplicationAndApprovalStatusDTO> honeyPotApprovedList = honeyPotService.getHoneyPotApprovedList();

        return ResponseEntity.ok(honeyPotApprovedList);
    }

    // 허니팟 Status 변경(모임날짜가 지났을 때 진행완료로 변경)
    @Operation(summary = "허니팟 상태 업데이트", description = "허니팟의 상태를 주기적으로 업데이트합니다.")
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateClosureStatus() {
        honeyPotService.updateClosureStatus();
    }

    // 허니팟 신고 하기
    @Operation(summary = "허니팟 신고", description = "특정 허니팟을 신고합니다.")
    @Parameter(name = "honeypotCode", description = "허니팟 게시물 번호", in = ParameterIn.PATH)
    @PutMapping("/report/{honeypotCode}")
    public ResponseEntity<HoneypotResponseMessage> reportHoneypot(@PathVariable int honeypotCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        HoneypotDTO reportedHoneypot = honeyPotService.reportHoneypot(honeypotCode);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("reportCount", reportedHoneypot.getReportCount());

        return ResponseEntity.ok().headers(headers).body(new HoneypotResponseMessage(200, "신고 성공", responseMap));
    }












}
