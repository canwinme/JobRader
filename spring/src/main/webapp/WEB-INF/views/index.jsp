<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>잡레이더 JobRadar</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        .hero { background: #1E2761; color: white; padding: 70px 20px; text-align: center; }
        .hero h1 { margin: 0 0 10px; font-size: 34px; }
        .hero p { margin: 0; color: #CADCFC; font-size: 16px; }
        .badge { display: inline-block; background: #E8871E; color: white; font-size: 12px; font-weight: bold;
                 padding: 4px 12px; border-radius: 12px; letter-spacing: 1px; margin-bottom: 14px; }
        .container { max-width: 1000px; margin: -40px auto 60px; padding: 0 20px; }
        .cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
        .card { background: white; border-radius: 10px; box-shadow: 0 4px 14px rgba(0,0,0,0.08);
                padding: 26px; text-decoration: none; color: inherit; transition: transform .15s; }
        .card:hover { transform: translateY(-4px); box-shadow: 0 8px 20px rgba(0,0,0,0.12); }
        .card h3 { margin: 0 0 8px; color: #1E2761; font-size: 17px; }
        .card p { margin: 0; font-size: 13px; color: #6B7280; line-height: 1.5; }
        .card .icon { font-size: 26px; margin-bottom: 10px; }
        @media (max-width: 720px) { .cards { grid-template-columns: repeat(2, 1fr); } }
    </style>
</head>
<body>
    <div class="hero">
        <div class="badge">DATA-DRIVEN INDUSTRY ANALYSIS</div>
        <h1>잡레이더 JobRadar</h1>
        <p>산업별 취업시장 분석 및 진로 탐색 지원 시스템</p>
    </div>

    <div class="container">
        <div class="cards">
            <a class="card" href="${pageContext.request.contextPath}/view/ranking">
                <div class="icon">📊</div>
                <h3>산업별 순위</h3>
                <p>최신 시점 취업자 증감률 기준 산업별 순위를 확인합니다.</p>
            </a>
            <a class="card" href="${pageContext.request.contextPath}/view/trend/ALL">
                <div class="icon">📈</div>
                <h3>증감률 추이</h3>
                <p>산업별 취업자 수·증감률의 시계열 변화, 성별 비교까지 그래프로 봅니다.</p>
            </a>
            <a class="card" href="${pageContext.request.contextPath}/view/industry-detail">
                <div class="icon">🏭</div>
                <h3>산업 분류 상세</h3>
                <p>6개 대분류 안에 실제로 어떤 세부 산업이 포함되는지 확인합니다.</p>
            </a>
            <a class="card" href="${pageContext.request.contextPath}/view/jobsearch">
                <div class="icon">🔍</div>
                <h3>세부 직업 탐색</h3>
                <p>산업별 세부 직업, 관련자격증·직업훈련·정규교육과정을 찾아봅니다.</p>
            </a>
            <a class="card" href="${pageContext.request.contextPath}/view/recommend">
                <div class="icon">🧭</div>
                <h3>진로 추천</h3>
                <p>성장 산업 연계 유망 직업, 적성유형(직군) 기반 직업 매칭을 받아봅니다.</p>
            </a>
            <a class="card" href="${pageContext.request.contextPath}/view/board">
                <div class="icon">💬</div>
                <h3>커뮤니티</h3>
                <p>산업별로 글을 작성하고 첨부파일을 공유하는 소통 공간입니다.</p>
            </a>
            <a class="card" href="${pageContext.request.contextPath}/view/expansion">
                <div class="icon">🚀</div>
                <h3>향후 확장 방안</h3>
                <p>산업별 경쟁도 분석, 청년·고령 맞춤 지원 등 확장 예정 기능을 미리 봅니다.</p>
            </a>
        </div>
    </div>
</body>
</html>