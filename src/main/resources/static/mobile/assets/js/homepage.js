$(document).ready(function () {
    // Check if session is validated with a user
    isValidated();

    function isValidated() {
        const LOGIN_REQUIRED = "LOGIN_REQUIRED";
        const token = sessionStorage.getItem('accessToken');
        if (token === null) {
            window.location.href = 'app-login.html';
        }
        $.ajax({
            type: "GET",
            url: "/check_login",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (authCredentialDTO) {
                console.log("User Logged with accessToken {} ", authCredentialDTO.firstName, authCredentialDTO.lastName, " username -> ", authCredentialDTO.username);
                $('#usernameMenu').text(`${authCredentialDTO.username}`);
                $('#nameMenu').text(`${authCredentialDTO.firstName} ${authCredentialDTO.lastName}`);
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
                        title: "<span style='color:#2D2C2C'>Sessione Scaduta, reinderizzazione...</span>",
                    })
                    setTimeout(function () {
                        window.location.href = "app-login.html";
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
                $('#reportStatement1').text('€ ' + statementReportDTO.statementTotal.toFixed(2));
                $('.sincetotalecapitale').text(" Performance Since " + statementReportDTO.beforeLastDate);
                $('.sincetotalecapitale1').text(" PIL Since " + statementReportDTO.beforeLastDate);
                // PIL
                var statementTotPercent = statementReportDTO.statementTotalPercent;
                if (statementReportDTO.pil > 0) {
                    $(`<span class="text-success mr-2"><i class="fa fa-arrow-up"></i> ${statementTotPercent}%</span>`).appendTo(`.performanceLastStatement`)
                    $('#pil').text("€ " + statementReportDTO.pil.toFixed(2)).addClass('text-success');
                } else if (statementReportDTO.pil === 0) {
                    $(`<span class="text-warning mr-2"><i class="fa fa-arrow-down"></i> ${statementTotPercent}%</span>`).appendTo(`.performanceLastStatement`)
                    $('#pil').text("€ " + statementReportDTO.pil.toFixed(2)).addClass('text-warning');
                } else {
                    $(`<span class="text-danger mr-2"><i class="fa fa-arrow-down"></i> ${statementTotPercent}%</span>`).appendTo(`.performanceLastStatement`)
                    $('#pil').text("€ " + statementReportDTO.pil.toFixed(2)).addClass('text-danger');
                }
                // PIL TOTALE
                if (statementReportDTO.pilTotal > 0) {
                    $(`<span class="text-success h2 font-weight-bold mb-0"><i class="fa fa-arrow-up"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
                    $('#pilTotale').text("€ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-success');
                } else if (statementReportDTO.pilTotal === 0) {
                    $(`<span class="text-warning h2 font-weight-bold mb-0"><i class="fa fa-arrow-down"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
                    $('#pilTotale').text("€ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-warning');
                } else {
                    $(`<span class="text-danger h2 font-weight-bold mb-0"><i class="fa fa-arrow-down"></i> ${statementReportDTO.pilPerformance.toFixed(0)}%</span>`).appendTo(`.performanceFirstDate`)
                    $('#pilTotale').text("€ " + statementReportDTO.pilTotal.toFixed(2)).addClass('text-danger');
                }
                //-----------------------------------------------------------------------
                // Get all Data for Graph Homepage
                //-----------------------------------------------------------------------
                var currentYear = "";
                var lastDate = "";
                var listDateForTable = [];
                var newDate = "";
                var dateFixed = "";
                for (let i = 0; i < statementReportDTO.listDate.length; i++) {
                    // Calcolo anno corrente(mi serve per la lista di date secondo anno)
                    currentYear = statementReportDTO.listDate[statementReportDTO.listDate.length - 1].split("-")[2];
                    $('#year').text('Graph ' + currentYear);
                    dateFixed = statementReportDTO.listDate[i].split("-");
                    newDate = dateFixed[2] + "-" + dateFixed[1] + "-" + dateFixed[0];
                    listDate += [statementReportDTO.listDate[i] + ","];
                    lastDate = statementReportDTO.listDate[i];
                    listDateForTable += [newDate + ","];
                }
                for (let i = 0; i < statementReportDTO.statementList.length; i++) {
                    statementList += [statementReportDTO.statementList[i] + ","];
                }
                for (let i = 0; i < statementReportDTO.listPil.length; i++) {
                    listPil += [statementReportDTO.listPil[i] + ","];
                }
                getGraph(listDateForTable, statementList, listPil);
                getGraphWallet(lastDate);
                getCurrentStatement(lastDate);
                getDate(listDate);
                //------------------------------------------------------------------------
                // END DATA HOMEPAGE
                //------------------------------------------------------------------------
            }
        });
    }

    /*--------------------------------------------------------------------------
    * Get Graph on index.html Area Chart
    * Provided by ApexChart, needs CSS JS and HTML container
    *--------------------------------------------------------------------------*/
    var graphDate = [];
    var graphValues = [];
    var graphPIL = [];

    function getGraph(listDateForTable, statementList, listPil) {
        graphDate = listDateForTable.split(",");
        graphValues = statementList.split(",");
        graphPIL = listPil.split(",");
        graphDate.pop();
        // GRAFICO
        // Graph
        var options = {
            series: [{
                name: 'Capitali Totali',
                data: graphValues,
            }, {
                name: 'PIL',
                data: graphPIL
            }],
            chart: {
                height: 350,
                type: 'area'
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'smooth'
            },
            xaxis: {
                type: 'datetime',
                categories: graphDate
            },
            tooltip: {
                x: {
                    format: 'dd-MM-yy'
                },
            },
        };

        var chart = new ApexCharts(document.querySelector("#chart"), options);
        chart.render();
    }

    /*--------------------------------------------------------------------------
    * End Graph Area Chart
    *--------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------
    * Get Graph on index.html Pie Chart
    * Provided by ApexChart, needs CSS JS and HTML container
    *--------------------------------------------------------------------------*/
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
                let splitName = nameWallet.split(",");
                let splitWallet = statementWallet.split(",").map(Number);
                splitName.pop();
                splitWallet.pop();
                var pie = {
                    series: splitWallet,
                    chart: {
                        width: 380,
                        type: 'pie',
                    },
                    labels: splitName,
                    responsive: [{
                        breakpoint: 480,
                        options: {
                            chart: {
                                width: 400
                            },
                            legend: {
                                position: 'right'
                            }
                        }
                    }]
                };

                var chart = new ApexCharts(document.querySelector("#pieChart"), pie);
                chart.render();
            }
        })
    }

    /*--------------------------------------------------------------------------
    * End Graph Area Chart
    *--------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------
    * List Wallet Homepage
    *--------------------------------------------------------------------------*/
    function getCurrentStatement(lastDate) {
        var walletIndex = $('#walletList');
        $.ajax({
            type: "GET",
            url: `/statement/listStatementDate/${lastDate}`,
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (statementTab) {
                for (let y = 0; y < statementTab.length; y++) {
                    $(`<li class="splide__slide">
                <div class="card-block bg-primary">
                <div class="card-main">
                    <div class="card-button">
                      <ion-icon name="ellipsis-horizontal"></ion-icon>
                    </div>
                    <div class="balance">
                        <span class="label">BALANCE</span>
                        <h1 class="title">€ ${statementTab[y].value}</h1>
                    </div>
                    <div class="in">
                        <div class="card-number">
                            <span class="label">Wallet Name</span>
                            ${statementTab[y].wallet.name}
                        </div>
                        <div class="bottom">
                            <div class="card-expiry">
                                <span class="label">Date Statement</span>
                                ${lastDate}
                            </div>
                            <div class="card-ccv">
                                <span class="label">User</span>
                                ${statementTab[y].user.firstName} ${statementTab[y].user.lastName}
                            </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </li>`).appendTo(walletIndex);

                }
                /*--------------------------------------------------------------------------
                * Splice Slider Effect Carousel Wallet
                *--------------------------------------------------------------------------*/
                var elms = document.getElementsByClassName('carousel-wallet');
                for (var i = 0, len = elms.length; i < len; i++) {
                    new Splide(elms[i], {
                        perPage: 3,
                        rewind: true,
                        gap: 16,
                        padding: 16,
                        arrows: false,
                        pagination: false,
                        breakpoints: {
                            768: {
                                perPage: 1
                            },
                            991: {
                                perPage: 2
                            }
                        }
                    }).mount();
                }
                /*--------------------------------------------------------------------------
                * END Splice Slider Effect Carousel Wallet
                *--------------------------------------------------------------------------*/
            }
        })
    }

    /*--------------------------------------------------------------------------
    *  END Splice Slider Effect Carousel Wallet
    *--------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------
    *  Get List Date For Statement By Date
    *--------------------------------------------------------------------------*/
    var splitDate = [];

    function getDate(listDate) {
        splitDate = listDate.split(",");
        splitDate.pop();
        const listStatementByDate = $('#dateOption');
        for (let i = splitDate.length - 1; i >= 0; i--) {
            $(`<li>
                  <a id='dateTest'>${splitDate[i]}</a>
                </li>`).hide().appendTo(listStatementByDate).fadeIn(i * 20);
        }
        localStorage.setItem('listDate', splitDate)
    }

    $('.dataConfirm').click(function () {
        const data = $('#dateTest').text();
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
            title: `<span style='color:#2D2C2C'>Date ${data}, redirect...</span>`
        })
        setTimeout(function () {
            window.location.href = "app-statement-by-date.html";
        }, 1000);
    })
    $('.resetCookies').on('click', function resetCookies() {
        document.cookie.split(";").forEach(function (c) {
            document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
        });
    })
    /*--------------------------------------------------------------------------
      *  END Get List Date For Statement By Date
      *--------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------
    *  Switch Desktop Mode
    *--------------------------------------------------------------------------*/
    $('#SwitchDesktop').click(function () {
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
            window.location.href = "../homepage.html";
        }, 1000);
    });
    /*--------------------------------------------------------------------------
     *  Switch Desktop Mode
     *--------------------------------------------------------------------------*/
});