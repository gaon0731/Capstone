package org.boot.cap.service;

import org.boot.cap.dto.addPlan.DateRange;
import org.boot.cap.dto.addPlan.EipScheduleDTO;
import org.boot.cap.dto.addPlan.ToeicScheduleDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    public List<EipScheduleDTO> fetchEipSchedules() {
        List<EipScheduleDTO> scheduleList = new ArrayList<>();

        try {
            // ✅ Q-Net 기사 시험일정 페이지 URL
            String url = "https://www.q-net.or.kr/crf005.do?id=crf00503&jmCd=1320"; // 예시 (기사 시험)
            Document doc = Jsoup.connect(url).get();

            Elements rows = doc.select("table.tbl_type1 tbody tr"); // 표 구조 분석 후 클래스명 확인 필요

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() < 5) continue; // 필요한 컬럼수보다 작으면 skip

                String round = cols.get(0).text(); // 회차명
                DateRange paperApplication = parseDateRange(cols.get(1).text(), "정보처리기사 필기시험 원서 접수 기간");
                DateRange additionalPaperApplication = parseDateRange(cols.get(2).text(), "정보처리기사 필기시험 추가 접수 기간");
                DateRange paperExam = parseDateRange(cols.get(3).text(), "정보처리기사 필기시험 시행 기간");

                EipScheduleDTO dto = new EipScheduleDTO(
                        round,
                        paperApplication,
                        additionalPaperApplication,
                        paperExam
                );
                scheduleList.add(dto);
            }

        } catch (IOException e) {
            e.printStackTrace(); // 에러 처리 로직 추가 가능
        }

        return scheduleList;
    }

    private DateRange parseDateRange(String text, String title) {
        if (text == null || text.isEmpty() || !text.contains("~")) return null;
        String[] parts = text.split("~");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        try {
            LocalDate start = LocalDate.parse(parts[0].trim(), formatter);
            LocalDate end = LocalDate.parse(parts[1].trim(), formatter);
            return new DateRange(title, start, end);
        } catch (Exception e) {
            return null;
        }
    }

    public List<ToeicScheduleDTO> getToeicSchedules() {
        List<ToeicScheduleDTO> scheduleList = new ArrayList<>();
        try {
            // 크롤링할 실제 URL
            String url = "https://m.exam.toeic.co.kr/receipt/receiptStep1.php?sbGoodsType1=TOE#";
            Document doc = Jsoup.connect(url).get();

            Elements examItems = doc.select("ul.exam_list li");
            for (Element item : examItems) {
                String examDate = item.select(".big_date em").text(); // 시험일
                String registrationText = item.select(".info_date").text(); // 접수기간

                // 예: "접수기간 : 2025.04.02(수) 10:00 ~ 2025.04.10(목) 13:00"
                String[] split = registrationText.replace("접수기간 :", "").trim().split("~");
                String registrationStart = split.length > 0 ? split[0].trim() : "";
                String registrationEnd = split.length > 1 ? split[1].trim() : "";

                ToeicScheduleDTO dto = new ToeicScheduleDTO(examDate, registrationStart, registrationEnd);
                scheduleList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace(); // 실제 환경에선 로그 처리
        }

        return scheduleList;
    }

}
