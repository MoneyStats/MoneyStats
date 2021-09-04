$(document).ready(function () {
  // Check if session is validated with a user
  isValidated();
  function isValidated (){
    const token = sessionStorage.getItem('accessToken');
    if (token === null) {
      window.location.href='loginPage.html';
    } 
  }
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
      }
    });
  }
});