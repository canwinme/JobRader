<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>산업별 증감률 추이 | 잡레이더</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; margin: 0; background: #F7F9FC; color: #36454F; }
        nav { background: #1E2761; padding: 16px 30px; }
        nav a { color: #CADCFC; text-decoration: none; margin-right: 22px; font-size: 14px; font-weight: bold; }
        nav a.active, nav a:hover { color: white; }
        .wrap { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1E2761; }
        .chips { margin-bottom: 12px; }
        .chips a, .gender-btn { display: inline-block; background: white; color: #1E2761; border: 1px solid #CADCFC;
                   border-radius: 16px; padding: 6px 16px; margin: 0 8px 8px 0; font-size: 13px; text-decoration: none;
                   font-weight: bold; cursor: pointer; }
        .chips a.current, .gender-btn.current { background: #E8871E; border-color: #E8871E; color: white; }
        .gender-row { margin-bottom: 20px; }
        .chart-card { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); padding: 24px; }
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
        <a href="${pageContext.request.contextPath}/view/trend/ALL" class="active">추이 그래프</a>
        <a href="${pageContext.request.contextPath}/view/industry-detail">산업 분류 상세</a>
        <a href="${pageContext.request.contextPath}/view/jobsearch">세부 직업 탐색</a>
        <a href="${pageContext.request.contextPath}/view/recommend" class="active">진로 추천</a>
        <a href="${pageContext.request.contextPath}/view/board">커뮤니티</a>
        <a href="${pageContext.request.contextPath}/view/expansion">향후 확장</a>
    </nav>

    <div class="wrap">
        <h2 id="title">취업자 증감률 추이</h2>

        <div class="chips">
            <a href="${pageContext.request.contextPath}/view/trend/ALL">전산업</a>
            <a href="${pageContext.request.contextPath}/view/trend/BC">광업.제조업</a>
            <a href="${pageContext.request.contextPath}/view/trend/F">건설업</a>
            <a href="${pageContext.request.contextPath}/view/trend/GI">도소매.음식숙박업</a>
            <a href="${pageContext.request.contextPath}/view/trend/ELS">사업.개인.공공서비스업</a>
            <a href="${pageContext.request.contextPath}/view/trend/DHJK">전기.운수.통신.금융업</a>
        </div>

        <div class="gender-row">
            취업자 수 성별 보기:
            <span class="gender-btn current" data-gender="ALL">전체</span>
            <span class="gender-btn" data-gender="M">남자</span>
            <span class="gender-btn" data-gender="F">여자</span>
        </div>

        <div class="chart-card">
            <canvas id="growthChart"></canvas>
        </div>
    </div>
     <div class="source">출처: 통계청 KOSIS 경제활동인구조사 (한국표준산업분류 KSIC 기준)</div>
    <script>
        const industryCd = "${industryCd}";
        const industryNames = {
            "ALL": "전산업", "BC": "광업.제조업", "F": "건설업",
            "GI": "도소매.음식숙박업", "ELS": "사업.개인.공공서비스업", "DHJK": "전기.운수.통신.금융업"
        };
        const industryNm = industryNames[industryCd] || industryCd;
        document.getElementById("title").innerText = industryNm + " 취업자 증감률 추이";
        document.querySelectorAll(".chips a").forEach(a => {
            if (a.href.endsWith("/" + industryCd)) a.classList.add("current");
        });

        let chart = null;
        let growthData = null; 

        function renderChart(genderCd) {
            const empUrl = "/api/industries/" + industryCd + "/employment-by-gender?gender=" + genderCd;
            fetch(empUrl).then(res => res.json()).then(empData => {
                const labels = empData.map(d => d.periodYear + "년 " + d.periodQuarter + "분기");
                const employedCounts = empData.map(d => d.employedCount);

                const genderLabel = genderCd === "ALL" ? "전체" : (genderCd === "M" ? "남자" : "여자");

                const datasets = [
                    { label: "취업자 수(" + genderLabel + ", 천 명)", data: employedCounts, borderColor: "#1E2761", yAxisID: "yCount", tension: 0.2 }
                ];
                if (growthData) {
                    datasets.push({ label: "취업자 증감률(%, 전체 기준)", data: growthData, borderColor: "#E8871E", yAxisID: "yRate", tension: 0.2 });
                }

                if (chart) chart.destroy();
                chart = new Chart(document.getElementById("growthChart"), {
                    type: "line",
                    data: { labels: labels, datasets: datasets },
                    options: {
                        scales: {
                            yCount: { type: "linear", position: "left", title: { display: true, text: "취업자 수" } },
                            yRate: { type: "linear", position: "right", title: { display: true, text: "증감률(%)" }, grid: { drawOnChartArea: false } }
                        }
                    }
                });
            });
        }

        // 증감률(전체 기준)은 한 번만 받아서 계속 재사용
        fetch("/api/industries/" + industryCd + "/growth-trend")
            .then(res => res.json())
            .then(data => {
                growthData = data.map(d => d.growthRate);
                renderChart("ALL");
            });

        document.querySelectorAll(".gender-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                document.querySelectorAll(".gender-btn").forEach(b => b.classList.remove("current"));
                btn.classList.add("current");
                renderChart(btn.dataset.gender);
            });
        });
    </script>
</body>
</html>