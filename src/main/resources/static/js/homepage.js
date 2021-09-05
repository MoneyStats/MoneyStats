$(document).ready(function () {
  // Check if session is validated with a user
  isValidated();
  function isValidated (){
    const token = sessionStorage.getItem('accessToken');
    if (token === null) {
      window.location.href='loginPage.html';
    } 
  }
  var listDate = [];
  var statementList = [];
  var listPil = [];
  getReportHomepage();
  function getReportHomepage(){
    $.ajax({
      type: "GET",
      url: "/statement/reportHomepage",
      contentType: 'application/json',
      dataType: 'json',
      headers: {
        Authorization: sessionStorage.getItem('accessToken')
      },
      //header: sessionStorage.getItem('accessToken'),
      success: function (statementReportDTO) {
        // Card Homepage
        $('#reportStatement').text('€ ' + statementReportDTO.statementTotal.toFixed(2));
        $('.sincetotalecapitale').text("Since " + statementReportDTO.beforeLastDate);
        // PIL
        if (statementReportDTO.pil > 0){
          $(`<span class="text-success mr-2"><i class="fa fa-arrow-up"></i> ${statementReportDTO.statementTotalPercent.toFixed(2)}%</span>`).appendTo(`.performanceLastStatement`)
          $('#pil').text("£ " + statementReportDTO.pil.toFixed(2)).addClass('text-success');
        } else if (statementReportDTO.pil === 0){
          $(`<span class="text-warning mr-2"><i class="fa fa-arrow-down"></i> ${statementReportDTO.statementTotalPercent.toFixed(2)}%</span>`).appendTo(`.performanceLastStatement`)
          $('#pil').text("£ " + statementReportDTO.pil.toFixed(2)).addClass('text-warning');
        } else {
          $(`<span class="text-danger mr-2"><i class="fa fa-arrow-down"></i> ${statementReportDTO.statementTotalPercent.toFixed(2)}%</span>`).appendTo(`.performanceLastStatement`)
          $('#pil').text("£ " + statementReportDTO.pil.toFixed(2)).addClass('text-danger');
        }
        // PIL TOTALE
        if (statementReportDTO.pilTotal > 0){
          $(`<span class="text-success h2 font-weight-bold mb-0"><i class="fa fa-arrow-up"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
          $('#pilTotale').text("£ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-success');
        } else if (statementReportDTO.pilTotal === 0){
          $(`<span class="text-warning h2 font-weight-bold mb-0"><i class="fa fa-arrow-down"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
          $('#pilTotale').text("£ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-warning');
        } else {
          $(`<span class="text-danger h2 font-weight-bold mb-0"><i class="fa fa-arrow-down"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
          $('#pilTotale').text("£ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-danger');
        }

        // Graph Homepage
        var currentYear = "";
        var lastDate = "";
        for (let i = 0; i < statementReportDTO.listDate.length; i++) {
          // Calcolo anno corrente(mi serve per la lista di date secondo anno)
          currentYear = statementReportDTO.listDate[statementReportDTO.listDate.length-1].split("-")[0];
          $('#year').text('Andamento Anno ' + currentYear);
          $('#listStatement').text('Statement Anno ' + currentYear);
          listDate += [statementReportDTO.listDate[i] + ","];
          lastDate = statementReportDTO.listDate[i];
        }
        $('#dataAttuale').text("Grafico Capitali in Data " + lastDate).val(lastDate)
        for (let i = 0; i < statementReportDTO.statementList.length; i++){
          statementList += [statementReportDTO.statementList[i] + ","];
        }
        for (let i = 0; i < statementReportDTO.listPil.length; i++){
          listPil += [statementReportDTO.listPil[i] + ","];
        }
        getGraph(listDate, statementList, listPil);
        getGraphWallet(lastDate);
        
      }
    });
  }
  var graphDate = [];
  var graphValues = [];
  var graphPIL = [];
   function getGraph(listDate, statementList, listPil) {
    graphDate = listDate.split(",");      
    graphValues = statementList.split(",");
    graphPIL = listPil.split(",");
        // GRAFICO
        // Graph
        var ctx = document.getElementById("myChart");

        var myChart = new Chart(ctx, {
          type: "line",
          data: {
            labels: graphDate,
            datasets: [
              {
                label: "Capitali Totali",
                data: graphValues,
                backgroundColor: "transparent",
                borderColor: "#007bff",
                borderWidth: 4,
                backgroundColor: [
                  'rgba(105, 0, 132, .2)',
                  ],
                  borderColor: [
                  'rgba(200, 99, 132, .7)',
                  ],
                  borderWidth: 2
              },
              {
                label: "PIL",
                data: graphPIL,
                backgroundColor: [
                'rgba(0, 137, 132, .2)',
                ],
                borderColor: [
                'rgba(0, 10, 130, .7)',
                ],
                borderWidth: 2
                }
            ],
          },
          
          options: {
            scales: {
              yAxes: [
                {
                  ticks: {
                    beginAtZero: true,
                  },
                },
              ],
            },
            legend: {
              display: true,
            },
          },
        });
  }
  var totLastDate;
  var nameWallet;
  var wallet;
  function getGraphWallet(lastDate){
    // TOTALE CAPITALI HOMEPAGE DATA ATTUALE
    $.ajax({
      type: "GET",
      url: `/statement/listStatementDate/${lastDate}`,
      contentType: 'application/json',
      dataType: 'json',
    headers: {
      Authorization: sessionStorage.getItem('accessToken')
    },
    //header: sessionStorage.getItem('accessToken'),
    success: function (listStatementDTO) {
      for (let i = 0; i < listStatementDTO.length; i++){
        totLastDate += listStatementDTO[i].value;
        wallet += [listStatementDTO[i].value + ","];
        nameWallet += [listStatementDTO[i].wallet.name + ','] ;
      }
      // Graph
      var ctx1 = document.getElementById("chart-pie");
      splitName = nameWallet.split(",");
      splitWallet = wallet.split(",");
      
      var myChart = new Chart(ctx1, {
        type: "pie",
        data: {
                labels: splitName,
                datasets: [{
                  data: splitWallet,
                  backgroundColor: [
                    "rgba(241, 182, 176, 0.6)",
                    "rgba(227, 192, 67, 0.6)",
                    "rgba(224, 99, 3, 0.9)",
                    "rgba(205, 157, 105, 0.1)",
                    "rgba(204, 107, 78, 0.8)",
                    "rgba(201, 199, 115, 0.3)",
                    "rgba(202, 149, 232, 0.2)",
                    "rgba(181, 223, 238, 0.6)",
                    "rgba(141, 88, 1, 0.2)",
                    "rgba(189, 9, 95, 0.1)",
                    "rgba(166, 73, 170, 0.5)",
                    "rgba(133, 159, 99, 0.3)",
                    "rgba(132, 211, 117, 0.9)",
                    "rgba(73, 197, 30, 0.7)",
                    "rgba(54, 16, 27, 0.6)",
                    "rgba(94, 187, 84, 0.7)",
                    "rgba(52, 127, 38, 0.5)",
                    "rgba(36, 26, 13, 0.2)",
                    "rgba(35, 81, 109, 0.3)",
                    "rgba(33, 130, 53, 0.8)",
                    "rgba(29, 60, 205, 0.8)",
                    "rgba(20, 242, 54, 0.3)",
                    "rgba(19, 26, 45, 0.4)",
                    "rgba(5, 158, 54, 0.2)",
                    "rgba(0, 138, 131, 0.3)",
                  ],
                }, ],
              }
            });
          }
        })
    }
});