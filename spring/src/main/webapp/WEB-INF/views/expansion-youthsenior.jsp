<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>향후 확장 방안 - 청년·고령 맞춤 지원 | 잡레이더</title>
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
        .section { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); padding: 24px; margin-bottom: 20px; }
        .flow { display: flex; flex-wrap: wrap; align-items: center; gap: 8px; margin-top: 10px; }
        .flow span { background: #EEF2FC; color: #1E2761; padding: 6px 14px; border-radius: 14px; font-size: 13px; font-weight: bold; }
        .flow .arrow { color: #E8871E; font-weight: bold; }
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
            <a href="${pageContext.request.contextPath}/view/expansion">① 산업별 경쟁도 분석</a>
            <a href="${pageContext.request.contextPath}/view/expansion/youth-senior" class="current">② 청년·고령 맞춤 지원</a>
        </div>

        <div class="notice">
            ⚠ 실제 데이터 연동 전 단계로, 고용24 직무데이터사전(NCS)·고용24 채용정보 API 신청 후 연동 예정인 기능입니다.
        </div>

        <div class="section">
            <h3 style="margin-top:0; color:#1E2761;">청년층 맞춤형 진로 지원</h3>
            <div class="flow">
                <span>보유 기술(Java/SQL)</span><span class="arrow">→</span>
                <span>관련 직무</span><span class="arrow">→</span>
                <span>관련 산업 추천</span>
            </div>
            <p style="font-size:12px; color:#6B7280; margin-top:16px;">데이터 출처(예정): 한국고용정보원 고용24 직무데이터사전(NCS 능력단위)</p>
        </div>

        <div class="section">
            <h3 style="margin-top:0; color:#1E2761;">고령층 맞춤형 진로 지원</h3>
            <p style="font-size:13px;">통근거리 · 근무시간 · 체력요구 등 실질적 취업 조건 중심으로 정보 제공 예정</p>
            <p style="font-size:12px; color:#6B7280; margin-top:16px;">데이터 출처(예정): 한국고용정보원 고용24 채용정보 API (개별 공고 단위 상세 정보)</p>
        </div>
    </div>
</body>
</html>