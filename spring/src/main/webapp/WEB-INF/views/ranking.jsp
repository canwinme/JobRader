<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>산업별 취업시장 순위 | 잡레이더</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1E2761; }
        .top-card { background: #1E2761; color: white; border-radius: 10px; padding: 22px 28px; margin-bottom: 24px;
                    display: flex; justify-content: space-between; align-items: center; }
        .top-card .label { font-size: 13px; color: #CADCFC; }
        .top-card .name { font-size: 20px; font-weight: bold; margin-top: 4px; }
        .top-card .rate { font-size: 26px; font-weight: bold; color: #E8871E; }
        table { border-collapse: collapse; width: 100%; background: white; border-radius: 8px; overflow: hidden;
                box-shadow: 0 2px 10px rgba(0,0,0,0.06); }
        th, td { padding: 14px 16px; text-align: center; }
        th { background: #1E2761; color: white; font-size: 13px; }
        tr:nth-child(even) { background-color: #EEF2FC; }
        td a { color: #1E2761; font-weight: bold; text-decoration: none; }
        td a:hover { text-decoration: underline; }
        .up { color: #d32f2f; font-weight: bold; }
        .down { color: #1565c0; font-weight: bold; }
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
        <a href="${pageContext.request.contextPath}/view/ranking" class="active">순위</a>
        <a href="${pageContext.request.contextPath}/view/trend/ALL">추이 그래프</a>
        <a href="${pageContext.request.contextPath}/view/industry-detail">산업 분류 상세</a>
        <a href="${pageContext.request.contextPath}/view/jobsearch">세부 직업 탐색</a>
         <a href="${pageContext.request.contextPath}/view/recommend" class="active">진로 추천</a>
        <a href="${pageContext.request.contextPath}/view/board">커뮤니티</a>
        <a href="${pageContext.request.contextPath}/view/expansion">향후 확장</a>
    </nav>

    <div class="wrap">
        <h2>산업별 취업시장 순위</h2>

        <c:if test="${not empty rankings}">
            <div class="top-card">
                <div>
                    <div class="label">이번 분기 가장 활발한 산업</div>
                    <div class="name">${rankings[0].industryNm}</div>
                </div>
                <div class="rate">+${rankings[0].growthRate}%</div>
            </div>
        </c:if>

        <table>
            <tr>
                <th>순위</th>
                <th>산업명</th>
                <th>취업자 수(천 명)</th>
                <th>증감률(%)</th>
            </tr>
            <c:forEach var="item" items="${rankings}">
                <tr>
                    <td>${item.indRank}</td>
                    <td><a href="${pageContext.request.contextPath}/view/trend/${item.industryCd}">${item.industryNm}</a></td>
                    <td><fmt:formatNumber value="${item.employedCount}" pattern="#,###" /></td>
                    <td class="${item.growthRate >= 0 ? 'up' : 'down'}">
                        <c:if test="${item.growthRate >= 0}">+</c:if>${item.growthRate}%
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
    <div class="source">출처: 통계청 KOSIS 경제활동인구조사 (한국표준산업분류 KSIC 기준)</div>
</body>
</html>