<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${post.title} | 잡레이더</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 700px; margin: 30px auto; padding: 0 20px; }
        .card { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); padding: 26px; }
        .meta { color: #6B7280; font-size: 13px; margin-bottom: 18px; padding-bottom: 14px; border-bottom: 1px solid #EEF2FC; }
        h2 { color: #1E2761; margin-top: 0; }
        .content { white-space: pre-wrap; line-height: 1.6; min-height: 100px; }
        .attach { margin-top: 20px; padding: 10px 14px; background: #EEF2FC; border-radius: 6px; font-size: 13px; }
        .attach a { color: #1E2761; font-weight: bold; }
        .actions { margin-top: 24px; }
        .actions a, .actions button { display: inline-block; padding: 8px 18px; border-radius: 6px; font-size: 13px;
                font-weight: bold; text-decoration: none; margin-right: 8px; border: none; cursor: pointer; }
        .btn-list { background: #EEF2FC; color: #1E2761; }
        .btn-delete { background: #d32f2f; color: white; }
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
        <div class="card">
            <h2>${post.title}</h2>
            <div class="meta">[${post.industryNm}] ${post.author} · ${post.createdAtFormatted}</div>
            <div class="content">${post.content}</div>

            <c:if test="${not empty post.attachmentName}">
                <div class="attach">
                    📎 <a href="${pageContext.request.contextPath}/view/board/${post.postId}/download">${post.attachmentName}</a>
                </div>
            </c:if>

            <div class="actions">
                <a class="btn-list" href="${pageContext.request.contextPath}/view/board">목록으로</a>
                <form style="display:inline;" action="${pageContext.request.contextPath}/view/board/${post.postId}/delete" method="post"
                      onsubmit="return confirm('정말 삭제하시겠습니까?');">
                    <button type="submit" class="btn-delete">삭제</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>