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
        for (let i = 0; i < statementReportDTO.listDate.length; i++) {
          // Calcolo anno corrente(mi serve per la lista di date secondo anno)
          currentYear = statementReportDTO.listDate[statementReportDTO.listDate.length-1].split("-")[0];
          $('#year').text('Andamento Anno ' + currentYear);
          $('#listStatement').text('Statement Anno ' + currentYear);
          listDate += [statementReportDTO.listDate[i] + ","];
        }
        for (let i = 0; i < statementReportDTO.statementList.length; i++){
          statementList += [statementReportDTO.statementList[i] + ","];
        }
        for (let i = 0; i < statementReportDTO.listPil.length; i++){
          listPil += [statementReportDTO.listPil[i] + ","];
        }
        getGraph(listDate, statementList, listPil);
        
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

    /*for (let z = 0; z < graphValues.length; z++){
     piltot = graphValues[z] - firstValue;        
     arraypil += [piltot] + ",";
    }
      graphpil = arraypil.split(",");*/
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
});