<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>글쓰기 | 잡레이더</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 700px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1E2761; }
        .card { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); padding: 26px; }
        label { display: block; font-size: 13px; font-weight: bold; color: #1E2761; margin: 14px 0 6px; }
        select, input[type=text], textarea { width: 100%; padding: 9px 10px; border: 1px solid #CADCFC;
                border-radius: 6px; font-size: 14px; box-sizing: border-box; font-family: inherit; }
        textarea { height: 160px; resize: vertical; }
        .submit-btn { margin-top: 20px; background: #E8871E; color: white; border: none; padding: 10px 24px;
                      border-radius: 6px; font-size: 14px; font-weight: bold; cursor: pointer; }
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
        <h2>글쓰기</h2>
        <div class="card">
            <!-- enctype="multipart/form-data" 가 없으면 파일이 전송되지 않는다 -->
            <form action="${pageContext.request.contextPath}/view/board/write" method="post" enctype="multipart/form-data">
                <label>산업 분류</label>
                <select name="industryCd" required>
                    <option value="ALL">전산업</option>
                    <option value="BC">광업.제조업</option>
                    <option value="F">건설업</option>
                    <option value="GI">도소매.음식숙박업</option>
                    <option value="ELS">사업.개인.공공서비스업</option>
                    <option value="DHJK">전기.운수.통신.금융업</option>
                </select>

                <label>제목</label>
                <input type="text" name="title" required>

                <label>작성자</label>
                <input type="text" name="author" required>

                <label>내용</label>
                <textarea name="content" required></textarea>

                <label>첨부파일 (선택)</label>
                <input type="file" name="file">

                <button type="submit" class="submit-btn">등록</button>
            </form>
        </div>
    </div>
</body>
</html>