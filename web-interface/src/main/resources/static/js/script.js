$(document).ready(function(){
loadTemperatureChart()
loadHumidityChart()
loadCo2Chart()
});
function loadTemperatureChart() {
    const dateRange = getDateRange("temperature")
    renderChart();
    var chartUpdateBtn = document.getElementById("temperature_update_chart_btn");
    chartUpdateBtn.onclick = function() {
        renderChart();
    }
    function renderChart() {
        const datesForChart = dateRange.getDates();
        var from = datesForChart[0];
        from.setHours(0, 0, 0, 0);
        var to = datesForChart[1];
        to.setHours(23, 59, 59, 59);
        var request = new XMLHttpRequest();
        request.open("GET", "/temperatureChart?dateFrom=" + from.getTime() + "&dateTo=" + to.getTime());
        request.onreadystatechange = function () {
            if (request.readyState == 4 && request.status == 200) {
                var dataForChart = JSON.parse(request.responseText);
                var dateArray = [];
                var livingRoomTemperatureArray = [];
                var bedRoomTemperatureArray = [];
                for(var i = 0; i < dataForChart.length; i++) {
                    var temperatureLog = dataForChart[i];
                    var lastDate = dateArray.pop();
                    if(lastDate !== temperatureLog.measureTimestamp && lastDate !== undefined) {
                    dateArray.push(lastDate);
                    }
                    dateArray.push(temperatureLog.measureTimestamp);
                    if(temperatureLog.sensorId === "livingroom") {
                                        livingRoomTemperatureArray.push(temperatureLog.metric.value);
                    } else if(temperatureLog.sensorId === "bedroom") {
                                        bedRoomTemperatureArray.push(temperatureLog.metric.value);
                    }
                }
                Highcharts.chart('temperatureContainer', {
                                              title: {text: ""},
                                              xAxis: {categories: dateArray},
                                              series: [
                                              {
                                                data: livingRoomTemperatureArray,
                                                name: "living_room_temperature",
                                                lineWidth: 0.5,
                                                zoneAxis: "x",
                                                zones: getColoredChartZones(dataForChart),
                                                color: "#5CCCCC"
                                              },
                                              {
                                                 data: bedRoomTemperatureArray,
                                                 name: "bed_room_temperature",
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
            if(temperatureLog.sensorId !== "livingroom") {
            continue;
            }
            if(temperatureLog.heaterRelayState === true) {
                result.push({
                             value: i,
                             dashStyle: 'solid',
                             color: '#FFB273',
                             }
                            )
            } else {
                result.push({
                            value: i,
                            dashStyle: 'solid',
                            color: '#5CCCCC'
                            }
                           )
            }
        }
      return result;
    }
    function getColoredChartZones2(dataForChart) {
            var result = [];
            for(var i = 0; i < dataForChart.length; i++) {
                var temperatureLog = dataForChart[i];
                if(temperatureLog.sensorId !== "bedroom") {
                            continue;
                            }
                if(temperatureLog.heaterRelayState === true) {
                    result.push({
                                 value: i,
                                 dashStyle: 'solid',
                                 color: '#FF7373',
                                 }
                                 )
                } else {
                    result.push({
                                value: i,
                                dashStyle: 'solid',
                                color: '#67E667'
                                }
                                )
                }
            }
          return result;
        }}

function loadHumidityChart() {
    const dateRange = getDateRange("humidity")
    renderChart();
    var chartUpdateBtn = document.getElementById("humidity_update_chart_btn");
    chartUpdateBtn.onclick = function() {
        renderChart();
    }
    function renderChart() {
        const datesForChart = dateRange.getDates();
        var from = datesForChart[0];
        from.setHours(0, 0, 0, 0);
        var to = datesForChart[1];
        to.setHours(23, 59, 59, 59);
        var request = new XMLHttpRequest();
        request.open("GET", "/humidityChart?dateFrom=" + from.getTime() + "&dateTo=" + to.getTime());
        request.onreadystatechange = function () {
            if (request.readyState == 4 && request.status == 200) {
                var dataForChart = JSON.parse(request.responseText);
                var dateArray = [];
                var livingRoomHumidityArray = [];
                var bedRoomHumidityArray = [];
                for(var i = 0; i < dataForChart.length; i++) {
                    var humidityLog = dataForChart[i];
                    var lastDate = dateArray.pop();
                    if(lastDate !== humidityLog.measureTimestamp && lastDate !== undefined) {
                    dateArray.push(lastDate);
                    }
                    dateArray.push(humidityLog.measureTimestamp);
                    if(humidityLog.sensorId === "livingroom") {
                                        livingRoomHumidityArray.push(humidityLog.metric.value);
                    } else if(humidityLog.sensorId === "bedroom") {
                                        bedRoomHumidityArray.push(humidityLog.metric.value);
                    }
                }
                Highcharts.chart('humidityContainer', {
                                              title: {text: ""},
                                              xAxis: {categories: dateArray},
                                              series: [
                                              {
                                                data: livingRoomHumidityArray,
                                                name: "living_room_humidity",
                                                lineWidth: 0.5,
                                                color: "#5CCCCC"
                                              },
                                              {
                                                 data: bedRoomHumidityArray,
                                                 name: "bed_room_humidity",
                                                 lineWidth: 0.5,
                                                 color: "#67E667"
                                               }
                                              ],
                                              chart: {zoomType: "x"}
                                            });
            }
       }
    request.send();
    }
}

function loadCo2Chart() {
    const dateRange = getDateRange("co2")
    renderChart();
    var chartUpdateBtn = document.getElementById('co2_update_chart_btn');
    chartUpdateBtn.onclick = function() {
        renderChart();
    }
    function renderChart() {
        const datesForChart = dateRange.getDates();
        var from = datesForChart[0];
        from.setHours(0, 0, 0, 0);
        var to = datesForChart[1];
        to.setHours(23, 59, 59, 59);
        var request = new XMLHttpRequest();
        request.open("GET", "/co2Chart?dateFrom=" + from.getTime() + "&dateTo=" + to.getTime());
        request.onreadystatechange = function () {
            if (request.readyState == 4 && request.status == 200) {
                var dataForChart = JSON.parse(request.responseText);
                var dateArray = [];
                var livingRoomCo2Array = [];
                var bedRoomCo2Array = [];
                for(var i = 0; i < dataForChart.length; i++) {
                    var co2Log = dataForChart[i];
                    var lastDate = dateArray.pop();
                    if(lastDate !== co2Log.measureTimestamp && lastDate !== undefined) {
                    dateArray.push(lastDate);
                    }
                    dateArray.push(co2Log.measureTimestamp);
                    if(co2Log.sensorId === "livingroom") {
                                        livingRoomCo2Array.push(co2Log.metric.value);
                    } else if(co2Log.sensorId === "bedroom") {
                                        bedRoomCo2Array.push(co2Log.metric.value);
                    }
                }
                Highcharts.chart('co2Container', {
                                              title: {text: ""},
                                              xAxis: {categories: dateArray},
                                              series: [
                                              {
                                                data: livingRoomCo2Array,
                                                name: "living_room_co2ppm",
                                                lineWidth: 0.5,
                                                color: "#5CCCCC"
                                              },
                                              {
                                                 data: bedRoomCo2Array,
                                                 name: "bed_room_co2ppm",
                                                 lineWidth: 0.5,
                                                 color: "#67E667"
                                               }
                                              ],
                                              chart: {zoomType: "x"}
                                            });
            }
       }
    request.send();
    }
}

 function getDateRange(chartNamePrefix) {
     const elem = document.getElementById(chartNamePrefix + 'DateRange');
         const rangePicker = new DateRangePicker(elem, {
         });
         rangePicker.setDates(new Date(), new Date());
     return rangePicker;
 }
