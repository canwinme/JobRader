<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>세부 직업 탐색 | 잡레이더</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1E2761; }
        .industry-chips { margin-bottom: 16px; }
        .industry-chips a {
            display: inline-block; background: white; color: #1E2761; border: 1px solid #CADCFC;
            border-radius: 16px; padding: 6px 16px; margin: 0 8px 8px 0; font-size: 13px;
            text-decoration: none; font-weight: bold;
        }
        .industry-chips a.current { background: #E8871E; border-color: #E8871E; color: white; }
        .notice { background: #EEF2FC; border-left: 5px solid #1E2761; padding: 10px 16px; border-radius: 6px;
                   font-size: 12px; margin-bottom: 20px; color: #1E2761; }

        .profession-group { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06);
                             margin-bottom: 10px; overflow: hidden; }
        .profession-group summary { padding: 14px 20px; cursor: pointer; font-weight: bold; color: #1E2761;
                                     list-style: none; }
        .profession-group summary::-webkit-details-marker { display: none; }
        .profession-group summary .count { color: #6B7280; font-weight: normal; font-size: 12px; margin-left: 6px; }
        .job-item { border-top: 1px solid #EEF2FC; }
        .job-item summary { padding: 10px 20px; font-weight: normal; font-size: 14px; color: #36454F; cursor: pointer; }
        .job-detail { padding: 4px 20px 16px 20px; font-size: 13px; line-height: 1.5; }
        .job-detail .label { color: #E8871E; font-weight: bold; margin-top: 8px; display: block; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/view/">홈</a>
        <a href="${pageContext.request.contextPath}/view/ranking">순위</a>
        <a href="${pageContext.request.contextPath}/view/trend/ALL">추이 그래프</a>
        <a href="${pageContext.request.contextPath}/view/industry-detail">산업 분류 상세</a>
        <a href="${pageContext.request.contextPath}/view/jobsearch" class="active">세부 직업 탐색</a>
        <a href="${pageContext.request.contextPath}/view/recommend" class="active">진로 추천</a>
        <a href="${pageContext.request.contextPath}/view/board">커뮤니티</a>
        <a href="${pageContext.request.contextPath}/view/expansion">향후 확장</a>
    </nav>

    <div class="wrap">
        <h2>세부 직업 탐색</h2>

        <div class="notice">
            출처: 커리어넷(교육부·한국직업능력연구원) 직업정보 Open API — 실제 데이터입니다.
        </div>

        <div class="industry-chips">
            <a href="?industryCd=ALL" class="${selectedIndustry=='ALL' ? 'current' : ''}">전산업</a>
            <a href="?industryCd=F" class="${selectedIndustry=='F' ? 'current' : ''}">건설업</a>
            <a href="?industryCd=BC" class="${selectedIndustry=='BC' ? 'current' : ''}">광업.제조업</a>
            <a href="?industryCd=GI" class="${selectedIndustry=='GI' ? 'current' : ''}">도소매.음식숙박업</a>
            <a href="?industryCd=DHJK" class="${selectedIndustry=='DHJK' ? 'current' : ''}">전기.운수.통신.금융업</a>
            <a href="?industryCd=ELS" class="${selectedIndustry=='ELS' ? 'current' : ''}">사업.개인.공공서비스업</a>
        </div>

        <c:forEach var="entry" items="${groupedJobs}">
            <details class="profession-group">
                <summary>${entry.key} <span class="count">(${fn:length(entry.value)}개)</span></summary>
                <c:forEach var="j" items="${entry.value}">
                    <details class="job-item">
                        <summary>${j.job}</summary>
                        <div class="job-detail">
                            <c:if test="${not empty j.certification}">
                                <span class="label">관련자격증</span>${j.certification}
                            </c:if>
                            <c:if test="${not empty j.training}">
                                <span class="label">직업훈련</span>${j.training}
                            </c:if>
                            <c:if test="${not empty j.preparation}">
                                <span class="label">정규교육과정</span>${j.preparation}
                            </c:if>
                        </div>
                    </details>
                </c:forEach>
            </details>
        </c:forEach>
    </div>
</body>
</html>