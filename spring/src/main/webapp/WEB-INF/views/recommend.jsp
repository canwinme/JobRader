<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>진로 추천 | 잡레이더</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1E2761; }
        h3 { color: #1E2761; margin-top: 30px; }
        .section { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); padding: 24px; margin-bottom: 20px; }
        .industry-card { border-left: 4px solid #E8871E; padding: 10px 16px; margin-bottom: 14px; background: #EEF2FC; border-radius: 6px; }
        .industry-card .rate { color: #E8871E; font-weight: bold; }
        .job-chip { display: inline-block; background: #1E2761; color: white; padding: 5px 12px; border-radius: 12px;
                    font-size: 13px; margin: 3px 6px 3px 0; }
        select, button { padding: 8px 12px; border-radius: 6px; border: 1px solid #CADCFC; font-size: 14px; }
        button { background: #E8871E; color: white; border: none; font-weight: bold; cursor: pointer; margin-left: 8px; }
        .job-item { border-top: 1px solid #EEF2FC; padding: 10px 0; }
        .job-item summary { font-weight: bold; cursor: pointer; }
        .job-detail { padding: 6px 10px 4px; font-size: 13px; line-height: 1.5; }
        .job-detail .label { color: #E8871E; font-weight: bold; display: block; margin-top: 6px; }
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
        <h2>진로 추천</h2>

        <div class="section">
            <h3 style="margin-top:0;">① 지금 뜨는 산업의 유망 직업</h3>
            <p style="font-size:13px; color:#6B7280;">최신 취업자 증감률 상위 3개 산업을 기준으로 대표 직업을 보여드려요.</p>
            <c:forEach var="idx" items="${topIndustries}">
                <div class="industry-card">
                    <b>${idx.industryNm}</b> <span class="rate">+${idx.growthRate}%</span>
                    <div style="margin-top:8px;">
                        <c:forEach var="j" items="${growthJobs[idx.industryCd]}">
                            <span class="job-chip">${j.job}</span>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
        </div>

        <div class="section">
            <h3 style="margin-top:0;">② 적성유형으로 찾기</h3>
            <p style="font-size:13px; color:#6B7280;">관심 있는 직군(적성유형)을 고르면, 산업 구분 없이 해당 유형의 직업을 전부 보여드려요.</p>
            <form method="get">
                <select name="profession">
                    <option value="">-- 직군 선택 --</option>
                    <c:forEach var="p" items="${professionList}">
                        <option value="${p}" ${p == selectedProfession ? 'selected' : ''}>${p}</option>
                    </c:forEach>
                </select>
                <button type="submit">찾기</button>
            </form>

            <c:if test="${not empty selectedProfession}">
                <h4 style="margin-top:20px;">"${selectedProfession}" 직업 목록</h4>
                <c:forEach var="j" items="${aptitudeJobs}">
                    <details class="job-item">
                        <summary>${j.job} <span style="font-size:12px; color:#9AA5B1; font-weight:normal;">(${j.industryCd})</span></summary>
                        <div class="job-detail">
                            <c:if test="${not empty j.certification}"><span class="label">관련자격증</span>${j.certification}</c:if>
                            <c:if test="${not empty j.training}"><span class="label">직업훈련</span>${j.training}</c:if>
                            <c:if test="${not empty j.preparation}"><span class="label">정규교육과정</span>${j.preparation}</c:if>
                        </div>
                    </details>
                </c:forEach>
            </c:if>
        </div>
    </div>
</body>
</html>