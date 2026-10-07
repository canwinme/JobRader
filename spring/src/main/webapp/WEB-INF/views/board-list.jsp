<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>커뮤니티 | 잡레이더</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1E2761; }
        .filters { margin-bottom: 16px; }
        .filters a { display: inline-block; background: white; color: #1E2761; border: 1px solid #CADCFC;
                     border-radius: 16px; padding: 6px 16px; margin: 0 8px 8px 0; font-size: 13px; text-decoration: none; font-weight: bold; }
        .filters a.current { background: #E8871E; border-color: #E8871E; color: white; }
        .write-btn { float: right; background: #1E2761; color: white; padding: 8px 18px; border-radius: 6px;
                     text-decoration: none; font-size: 13px; font-weight: bold; }
        table { border-collapse: collapse; width: 100%; background: white; border-radius: 8px; overflow: hidden;
                box-shadow: 0 2px 10px rgba(0,0,0,0.06); }
        th, td { padding: 12px 14px; text-align: left; font-size: 14px; }
        th { background: #1E2761; color: white; font-size: 13px; }
        tr:nth-child(even) { background-color: #EEF2FC; }
        td a { color: #1E2761; font-weight: bold; text-decoration: none; }
        .empty { text-align: center; color: #6B7280; padding: 30px; background: white; border-radius: 8px; }
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
        <a class="write-btn" href="${pageContext.request.contextPath}/view/board/write">글쓰기</a>
        <h2>산업별 커뮤니티</h2>

        <div class="filters">
            <a href="${pageContext.request.contextPath}/view/board" class="${empty selectedIndustry ? 'current' : ''}">전체</a>
            <a href="${pageContext.request.contextPath}/view/board?industryCd=ALL" class="${selectedIndustry=='ALL' ? 'current' : ''}">전산업</a>
            <a href="${pageContext.request.contextPath}/view/board?industryCd=BC" class="${selectedIndustry=='BC' ? 'current' : ''}">광업.제조업</a>
            <a href="${pageContext.request.contextPath}/view/board?industryCd=F" class="${selectedIndustry=='F' ? 'current' : ''}">건설업</a>
            <a href="${pageContext.request.contextPath}/view/board?industryCd=GI" class="${selectedIndustry=='GI' ? 'current' : ''}">도소매.음식숙박업</a>
            <a href="${pageContext.request.contextPath}/view/board?industryCd=ELS" class="${selectedIndustry=='ELS' ? 'current' : ''}">사업.개인.공공서비스업</a>
            <a href="${pageContext.request.contextPath}/view/board?industryCd=DHJK" class="${selectedIndustry=='DHJK' ? 'current' : ''}">전기.운수.통신.금융업</a>
        </div>

        <c:choose>
            <c:when test="${empty posts}">
                <div class="empty">등록된 글이 없습니다. 첫 글을 남겨보세요.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <tr><th style="width:80px;">산업</th><th>제목</th><th style="width:100px;">작성자</th><th style="width:150px;">작성일</th></tr>
                    <c:forEach var="p" items="${posts}">
                        <tr>
                            <td>${p.industryNm}</td>
                            <td><a href="${pageContext.request.contextPath}/view/board/${p.postId}">${p.title}</a>
                                <c:if test="${not empty p.attachmentName}">&#128190;</c:if>
                            </td>
                            <td>${p.author}</td>
                            <td>${p.createdAtFormatted}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>