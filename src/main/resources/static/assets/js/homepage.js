$(document).ready(function () {

    const LOGIN_REQUIRED = "LOGIN_REQUIRED";

    //-------------------------------------------------------------
    // Check if session is validated with a user
    //-------------------------------------------------------------
    isValidated();

    function isValidated() {
        const token = sessionStorage.getItem('accessToken');
        if (token === null) {
            window.location.href = 'loginPage.html';
        }
        //-------------------------------------------------------------
        // Check if session is validated with a user
        //-------------------------------------------------------------
        $.ajax({
            type: "GET",
            url: "/check_login",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (authCredentialDTO) {
                console.log("User Logged with accessToken {}, ", authCredentialDTO.firstName, authCredentialDTO.lastName, " username -> ", authCredentialDTO.username);
                $('#options').text(`Opzioni - ${authCredentialDTO.username}`);
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.error;
                if (responseDTO === LOGIN_REQUIRED) {
                    const Toast = Swal.mixin({
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 1500,
                        timerProgressBar: true,
                    })
                    Toast.fire({
                        icon: 'error',
                        title: 'Sessione Scaduta, reinderizzazione...'
                    })
                    setTimeout(function () {
                        window.location.href = "loginPage.html";
                    }, 1500);
                }
            }
        })
        getReportHomepage();
    }

    //-------------------------------------------------------------
    // Invalidate Session on Press Log Out
    //-------------------------------------------------------------
    $('#logout').click(function (e) {
        sessionStorage.removeItem('accessToken');
    });
    //-------------------------------------------------------------
    // END
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    // Get Full Report Homepage
    //-------------------------------------------------------------
    var listDate = [];
    var statementList = [];
    var listPil = [];

    function getReportHomepage() {
        $.ajax({
            type: "GET",
            url: "/homepage/reportHomepage",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (statementReportDTO) {
                // Card Homepage
                $('#reportStatement').text('€ ' + statementReportDTO.statementTotal.toFixed(2));
                $('.sincetotalecapitale').text("Since " + statementReportDTO.beforeLastDate);
                // PIL
                var statementTotPercent = statementReportDTO.statementTotalPercent;
                if (statementTotPercent.countDecimals() > 0){
                    statementTotPercent = statementTotPercent.toFixed(2);
                }
                if (statementReportDTO.pil > 0) {
                    $(`<span class="text-success mr-2"><i class="fa fa-arrow-up"></i> ${statementTotPercent}%</span>`).appendTo(`.performanceLastStatement`)
                    $('#pil').text("£ " + statementReportDTO.pil.toFixed(2)).addClass('text-success');
                } else if (statementReportDTO.pil === 0) {
                    $(`<span class="text-warning mr-2"><i class="fa fa-arrow-down"></i> ${statementTotPercent}%</span>`).appendTo(`.performanceLastStatement`)
                    $('#pil').text("£ " + statementReportDTO.pil.toFixed(2)).addClass('text-warning');
                } else {
                    $(`<span class="text-danger mr-2"><i class="fa fa-arrow-down"></i> ${statementTotPercent}%</span>`).appendTo(`.performanceLastStatement`)
                    $('#pil').text("£ " + statementReportDTO.pil.toFixed(2)).addClass('text-danger');
                }
                // PIL TOTALE
                if (statementReportDTO.pilTotal > 0) {
                    $(`<span class="text-success h2 font-weight-bold mb-0"><i class="fa fa-arrow-up"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
                    $('#pilTotale').text("£ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-success');
                } else if (statementReportDTO.pilTotal === 0) {
                    $(`<span class="text-warning h2 font-weight-bold mb-0"><i class="fa fa-arrow-down"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
                    $('#pilTotale').text("£ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-warning');
                } else {
                    $(`<span class="text-danger h2 font-weight-bold mb-0"><i class="fa fa-arrow-down"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
                    $('#pilTotale').text("£ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-danger');
                }

                //-----------------------------------------------------------------------
                // Get all Data for Graph Homepage
                //-----------------------------------------------------------------------
                var currentYear = "";
                var lastDate = "";
                var listDateForTable;
                for (let i = 0; i < statementReportDTO.listDate.length; i++) {
                    // Calcolo anno corrente(mi serve per la lista di date secondo anno)
                    currentYear = statementReportDTO.listDate[statementReportDTO.listDate.length - 1].split("-")[2];
                    $('#year').text('Andamento Anno ' + currentYear);
                    $('#listStatement').text('Statement Anno ' + currentYear);
                    listDate += [statementReportDTO.listDate[i] + ","];
                    lastDate = statementReportDTO.listDate[i];
                    listDateForTable += [statementReportDTO.listDate[i]];
                }
                $('#dataAttuale').text("Grafico Capitali in Data " + lastDate).val(lastDate)
                for (let i = 0; i < statementReportDTO.statementList.length; i++) {
                    statementList += [statementReportDTO.statementList[i] + ","];
                }
                for (let i = 0; i < statementReportDTO.listPil.length; i++) {
                    listPil += [statementReportDTO.listPil[i] + ","];
                }
                getGraph(listDate, statementList, listPil);
                getGraphWallet(lastDate);
                getCurrentStatement(listDate, currentYear);
                getDate(listDate);
                //------------------------------------------------------------------------
                // END DATA HOMEPAGE
                //------------------------------------------------------------------------
            }
        });
    }
    //------------------------------------------------------------------------------
    //Check if number has decimal
    //------------------------------------------------------------------------------
    Number.prototype.countDecimals = function () {
        if(Math.floor(this.valueOf()) === this.valueOf()) return 0;
        return this.toString().split(".")[1].length || 0; 
    }

    //-------------------------------------------------------------
    // Get Line Graph Homepage
    //-------------------------------------------------------------
    var graphDate = [];
    var graphValues = [];
    var graphPIL = [];

    function getGraph(listDate, statementList, listPil) {
        graphDate = listDate.split(",");
        graphValues = statementList.split(",");
        graphPIL = listPil.split(",");
        graphDate.pop();
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

    //-------------------------------------------------------------
    // END Line Graph
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    // Get PieGraph Homepage
    //-------------------------------------------------------------
    var nameWallet = [];
    var statementWallet = [];

    function getGraphWallet(lastDate) {
        // TOTALE CAPITALI HOMEPAGE DATA ATTUALE
        $.ajax({
            type: "GET",
            url: `/homepage/getPieGraph/${lastDate}`,
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (listWalletStatementDTO) {
                for (let i = 0; i < listWalletStatementDTO.walletList.length; i++) {
                    nameWallet += [listWalletStatementDTO.walletList[i] + ","];
                    statementWallet += [listWalletStatementDTO.statementList[i] + ','];
                }

                // Graph
                var ctx1 = document.getElementById("chart-pie");
                let splitName = nameWallet.split(",");
                let splitWallet = statementWallet.split(",");
                splitName.pop(); // Delete last element of array

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
                        },],
                    }
                });
            }
        })
    }

    // LISTA STATEMENT
    function getWallet() {
        $.ajax({
            type: "GET",
            url: `/wallet/list`,
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (listWalletForHomepage) {
                const listWallet = $('#titoloTab');
                for (let i = 0; i < listWalletForHomepage.length; i++) {
                    $(`<th scope="col">${listWalletForHomepage[i].name}</th>`).hide().appendTo(listWallet).fadeIn(i * 20);

                }
            }
        })
    }

    getWallet();

    function getCurrentStatement(listDate, year) {
        let splitDate = listDate.split(",");
        const dataTabella = $('#data');
        for (let i = 0; i < splitDate.length; i++) {
            if (splitDate[i].includes(year)) {
                $.ajax({
                    type: "GET",
                    url: `/statement/listStatementDate/${splitDate[i]}`,
                    contentType: 'application/json',
                    dataType: 'json',
                    headers: {
                        Authorization: sessionStorage.getItem('accessToken')
                    },
                    success: function (statementTab) {
                        // DATA PER TABELLA
                        $(`<tr id="data${i}"><th scope="row">${splitDate[i]}</th></tr>`).hide().appendTo(dataTabella).fadeIn(i * 20);
                        const statTab = $(`#data${i}`)
                        // Fine calcolo
                        for (let y = 0; y < statementTab.length; y++) {
                            $(`<td>£ ${statementTab[y].value}</td>`).hide().appendTo(statTab).fadeIn(i * 20);
                        }
                    }
                })
            }
        }
    }

    var splitDate = [];

    function getDate(listDate) {
        splitDate = listDate.split(",");
        splitDate.pop();
        const listStatementByDate = $('#dateOption');
        for (let i = splitDate.length - 1; i >= 0; i--) {
            $(`<option id='dateSelect' value="${splitDate[i]}">${splitDate[i]}</option>`).hide().appendTo(listStatementByDate).fadeIn(i * 20);
        }
    }

    $('#dataConfirm').click(function () {
        const data = $('#dateOption').val();
        localStorage.setItem('date', data);
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1000,
            timerProgressBar: true,
        })

        Toast.fire({
            icon: 'success',
            title: 'Data inserite, reinderizzazione...'
        })
        setTimeout(function () {
            window.location.href = "capitalewallet.html";
        }, 1000);
    })
    $('.resetCookies').on('click', function resetCookies() {
        document.cookie.split(";").forEach(function (c) {
            document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
        });
    })

    /*--------------------------------------------------------------------------
      *  Switch Mobile Mode
      *--------------------------------------------------------------------------*/
    $('#SwitchMobile').click(function () {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1000,
            timerProgressBar: true,
        })
        Toast.fire({
            icon: 'info',
            title: "<span style='color:#2D2C2C'>Reinderizzazione...</span>",
        })
        setTimeout(function () {
            window.location.href = "mobile/index.html";
        }, 1000);
    });
    /*--------------------------------------------------------------------------
     *  Switch Mobile Mode
     *--------------------------------------------------------------------------*/
});