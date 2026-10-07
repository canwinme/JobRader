<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>향후 확장 방안 - 산업별 경쟁도 | 잡레이더</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1E2761; }
        .subnav { margin-bottom: 20px; }
        .subnav a { display: inline-block; background: white; color: #1E2761; border: 1px solid #CADCFC;
                    border-radius: 16px; padding: 6px 16px; margin: 0 8px 8px 0; font-size: 13px;
                    text-decoration: none; font-weight: bold; }
        .subnav a.current { background: #E8871E; border-color: #E8871E; color: white; }
        .notice { background: #FFF4E5; border-left: 5px solid #E8871E; padding: 14px 18px; border-radius: 6px;
                   font-size: 13px; margin-bottom: 26px; color: #7A4A00; }
        .section { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); padding: 24px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { padding: 10px 12px; text-align: center; font-size: 14px; }
        th { background: #EEF2FC; color: #1E2761; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/view/">홈</a>
        <a href="${pageContext.request.contextPath}/view/ranking">순위</a>
        <a href="${pageContext.request.contextPath}/view/trend/ALL">추이 그래프</a>
        <a href="${pageContext.request.contextPath}/view/industry-detail">산업 분류 상세</a>
        <a href="${pageContext.request.contextPath}/view/jobsearch">세부 직업 탐색</a>
        <a href="${pageContext.request.contextPath}/view/recommend" class="active">진로 추천</a>
        <a href="${pageContext.request.contextPath}/view/board">커뮤니티</a>
        <a href="${pageContext.request.contextPath}/view/expansion">향후 확장</a>
    </nav>

    <div class="wrap">
        <h2>향후 확장 방안</h2>

        <div class="subnav">
            <a href="${pageContext.request.contextPath}/view/expansion" class="current">① 산업별 경쟁도 분석</a>
            <a href="${pageContext.request.contextPath}/view/expansion/youth-senior">② 청년·고령 맞춤 지원</a>
        </div>

        <div class="notice">
            ⚠ 이 화면의 수치는 <b>실제 데이터가 아닌 더미(예시) 데이터</b>입니다.
            구직자 수요 데이터는 공개 통계로 제공되지 않아, 확장 기능의 형태를 미리 보여드리기 위한 목적으로만 사용했습니다.
        </div>

        <div class="section">
            <h3 style="margin-top:0; color:#1E2761;">산업별 경쟁도 분석 (더미 데이터)</h3>
            <table>
                <tr><th>산업명</th><th>가상 구직자 수</th><th>가상 채용 수요</th><th>경쟁도 지수</th></tr>
                <c:forEach var="row" items="${dummyCompetition}">
                    <tr>
                        <td>${row.industryNm}</td>
                        <td>${row.jobSeekers}</td>
                        <td>${row.vacancyDemand}</td>
                        <td>${row.competitionIndex}</td>
                    </tr>
                </c:forEach>
            </table>
            <p style="font-size:12px; color:#6B7280;">경쟁도 지수 = 가상 구직자 수 ÷ 가상 채용 수요. 값이 낮을수록(1 미만) 상대적으로 취업 기회가 높은 산업입니다.
            (구상 기준: 건설업은 실제 이직률 1위 추세를 반영해 기회가 많도록, 사업·개인·공공서비스업은 실제 취업자 증감률 1위 추세를 반영해 경쟁이 치열하도록 구상했습니다.)</p>
        </div>
    </div>
</body>
</html>