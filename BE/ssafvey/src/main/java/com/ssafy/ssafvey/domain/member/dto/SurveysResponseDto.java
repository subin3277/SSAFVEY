package com.ssafy.ssafvey.domain.member.dto;

import com.ssafy.ssafvey.domain.member.entity.Member;
import com.ssafy.ssafvey.domain.member.entity.MemberSurvey;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveysResponseDto {
    private List<RecentItem> surveys;

    public static Map<String, List<RecentItem>>  getSurveyParticipated(Member member){
        List<RecentItem> recentActivity = new ArrayList<>();


        RecentItem item1 = new RecentItem(1L,"가오갤은 명작인가","이정범", "2023.05.07");
        RecentItem item2 = new RecentItem(2L,"이유영은 짱짱인가","이유영", "2023.05.06");
        RecentItem item3 = new RecentItem(4L,"아침에 너무 배고픈데 아메추","김성수", "2023.05.07");

        recentActivity.add(item1);
        recentActivity.add(item2);
        recentActivity.add(item3);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Map<String, List<RecentItem>> itemMap = new HashMap<>();
        for (RecentItem item : recentActivity) {
            Date date = null;
            try {
                date = sdf.parse(item.getEndDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String dateString = sdf.format(date);
            if (itemMap.containsKey(dateString)) {
                itemMap.get(dateString).add(item);
            } else {
                List<RecentItem> list = new ArrayList<>();
                list.add(item);
                itemMap.put(dateString, list);
            }
        }

        return itemMap;
    }

    public static Map<String, List<RecentItem>> getSurveyCreated(Member member, List<MemberSurvey> createdMemberSurvey){
        List<RecentItem> recentActivity = new ArrayList<>();
        // member에서 membersurvey 가져오기 for문 돌려서 recentItem에 넣기 service에서 해야할듯?
        for (MemberSurvey memberSurvey : createdMemberSurvey) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String formattedDate = memberSurvey.getSurvey().getCreateDate().format(formatter);
            RecentItem tmpItem = new RecentItem(memberSurvey.getId(),memberSurvey.getSurvey().getTitle(),memberSurvey.getSurvey().getOrganization(),formattedDate);
            recentActivity.add(tmpItem);
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Map<String, List<RecentItem>> itemMap = new HashMap<>();
        for (RecentItem item : recentActivity) {
            Date date = null;
            try {
                date = sdf.parse(item.getEndDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String dateString = sdf.format(date);
            if (itemMap.containsKey(dateString)) {
                itemMap.get(dateString).add(item);
            } else {
                List<RecentItem> list = new ArrayList<>();
                list.add(item);
                itemMap.put(dateString, list);
            }
        }


        // 빌더 방식으로 DB에 저장
        return  itemMap;
    }
}
