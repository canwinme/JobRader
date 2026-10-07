<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>산업 분류 상세 | 잡레이더</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1E2761; }
        .notice { background: #EEF2FC; border-left: 5px solid #1E2761; padding: 14px 18px; border-radius: 6px;
                   font-size: 13px; margin-bottom: 26px; color: #1E2761; }
        .card { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06);
                padding: 22px 26px; margin-bottom: 18px; }
        .card h3 { margin: 0 0 12px; color: #1E2761; }
        .card h3 .code { background: #E8871E; color: white; font-size: 12px; padding: 3px 10px;
                          border-radius: 10px; margin-left: 8px; vertical-align: middle; }
        .card ul { margin: 0; padding-left: 20px; }
        .card li { font-size: 14px; padding: 4px 0; color: #36454F; }
        .source {
            position: fixed; right: 20px; bottom: 16px;
            font-size: 11px; color: #9AA5B1; background: rgba(255,255,255,0.85);
            padding: 4px 10px; border-radius: 10px;
        }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/view/">홈</a>
        <a href="${pageContext.request.contextPath}/view/ranking">순위</a>
        <a href="${pageContext.request.contextPath}/view/trend/ALL">추이 그래프</a>
        <a href="${pageContext.request.contextPath}/view/industry-detail" class="active">산업 분류 상세</a>
        <a href="${pageContext.request.contextPath}/view/jobsearch">세부 직업 탐색</a>
         <a href="${pageContext.request.contextPath}/view/recommend" class="active">진로 추천</a>
        <a href="${pageContext.request.contextPath}/view/board">커뮤니티</a>
        <a href="${pageContext.request.contextPath}/view/expansion">향후 확장</a>
    </nav>

    <div class="wrap">
        <h2>산업 분류 상세</h2>
        <div class="notice">
            여기 있는 6개 대분류는 여러 개의 세부 산업(KSIC 표준산업분류)을 하나로 묶은 값입니다.
            아래에서 각 대분류 안에 실제로 어떤 업종들이 포함되는지 확인할 수 있습니다.
        </div>

        <c:forEach var="d" items="${details}">
            <div class="card">
                <h3>${d.industryNm}<span class="code">${d.industryCd}</span></h3>
                <ul>
                    <c:forEach var="sub" items="${d.subIndustries}">
                        <li>${sub}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:forEach>
    </div>

    <div class="source">출처: 통계청 KOSIS 경제활동인구조사 (한국표준산업분류 KSIC 기준)</div>
</body>
</html>