document.addEventListener("DOMContentLoaded", function() {
    const fromDateInput = new Datepicker(document.getElementById("fromDateInput"));
    const toDateInput = new Datepicker(document.getElementById("toDateInput"));
    fromDateInput.config({
    initial_date: new Date(),
    format: d => {
              return [
                  d.getDate(),
                  d.getMonth(),
                d.getFullYear(),
              ].join("-");
          }
    });
    toDateInput.config({
    initial_date: new Date(),
    format: d => {
                return [
                    d.getDate(),
                    d.getMonth(),
                    d.getFullYear(),
                ].join("-");
           }
    });
    renderChart();
    var chartUpdateBtn = document.getElementById('update_chart_btn');
    chartUpdateBtn.onclick = function() {
        renderChart();
    }
    function renderChart() {
        var request = new XMLHttpRequest();
        request.open("GET", "/thermostatChart?dateFrom=" + fromDateInput.getDate().toISOString() + "&dateTo=" + toDateInput.getDate().toISOString());
        request.onreadystatechange = function () {
            if (request.readyState == 4 && request.status == 200) {
                var dataForChart = JSON.parse(request.responseText);
                var dateArray = [];
                var livingRoomTemperatureArray = [];
                var bedRoomTemperatureArray = [];
                for(var i = 0; i < dataForChart.length; i++) {
                    var temperatureLog = dataForChart[i];
                    dateArray[i] = temperatureLog.timeStamp;
                    livingRoomTemperatureArray[i] = temperatureLog.livingRoomTemperature;
                    bedRoomTemperatureArray[i] = temperatureLog.bedRoomTemperature;
                }
                Highcharts.chart('container', {
                                              title: {text: "Thermostat chart"},
                                              xAxis: {categories: dateArray},
                                              series: [
                                              {
                                                data: livingRoomTemperatureArray,
                                                name: "Living_Room_Temperature",
                                                lineWidth: 0.5,
                                                zoneAxis: "x",
                                                zones: getColoredChartZones(dataForChart),
                                                color: "#5CCCCC"
                                              },
                                              {
                                                 data: bedRoomTemperatureArray,
                                                 name: "Bed_Room_Temperature",
                                                 lineWidth: 0.5,
                                                 zoneAxis: "x",
                                                 zones: getColoredChartZones2(dataForChart),
                                                 color: "#67E667"
                                               }
                                              ],
                                              chart: {zoomType: "x"}
                                            });
            }
       }
    request.send();
    }
    function getColoredChartZones(dataForChart) {
        var result = [];
        for(var i = 0; i < dataForChart.length; i++) {
            var temperatureLog = dataForChart[i];
            if(temperatureLog.relayState === "WARM_UP") {
                result[i] = {
                             value: i,
                             dashStyle: 'solid',
                             color: '#FFB273',
                             }
            } else {
                result[i] = {
                            value: i,
                            dashStyle: 'solid',
                            color: '#5CCCCC'
                            }
            }
        }
      return result;
    }
    function getColoredChartZones2(dataForChart) {
            var result = [];
            for(var i = 0; i < dataForChart.length; i++) {
                var temperatureLog = dataForChart[i];
                if(temperatureLog.relayState === "WARM_UP") {
                    result[i] = {
                                 value: i,
                                 dashStyle: 'solid',
                                 color: '#FF7373',
                                 }
                } else {
                    result[i] = {
                                value: i,
                                dashStyle: 'solid',
                                color: '#67E667'
                                }
                }
            }
          return result;
        }
});