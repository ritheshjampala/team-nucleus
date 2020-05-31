$(function() {

    Morris.Area({
        element: 'morris-area-chart',
        data: [{
            period: '2016 Q1',
            Alexa: 2666,
            Chatbot: null,
            Phone: 2647
        }, {
            period: '2016 Q2',
            Alexa: 2778,
            Chatbot: 2294,
            Phone: 2441
        }, {
            period: '2016 Q3',
            Alexa: 4912,
            Chatbot: 1969,
            Phone: 2501
        }, {
            period: '2016 Q4',
            Alexa: 3767,
            Chatbot: 3597,
            Phone: 5689
        }, {
            period: '2017 Q1',
            Alexa: 6810,
            Chatbot: 1914,
            Phone: 2293
        }, {
            period: '2017 Q2',
            Alexa: 5670,
            Chatbot: 4293,
            Phone: 1881
        }, {
            period: '2017 Q3',
            Alexa: 4820,
            Chatbot: 3795,
            Phone: 1588
        }, {
            period: '2017 Q4',
            Alexa: 15073,
            Chatbot: 5967,
            Phone: 5175
        }, {
            period: '2018 Q1',
            Alexa: 10687,
            Chatbot: 4460,
            Phone: 2028
        }, {
            period: '2018 Q2',
            Alexa: 8432,
            Chatbot: 5713,
            Phone: 1791
        }],
        xkey: 'period',
        ykeys: ['Alexa', 'Chatbot', 'Phone'],
        labels: ['Video Advertisements', '3D Model Videos', '3D Models'],
        pointSize: 2,
        hideHover: 'auto',
        resize: true
    });

    Morris.Donut({
        element: 'morris-donut-chart',
        data: [{
            label: "MebThera",
            value: 112
        }, {
            label: "Avastin",
            value: 67
        }, {
            label: "Herceptin",
            value: 23
        },{
            label: "Xeloda",
            value: 99
        },{
            label: "Rofecoxib",
            value: 50
        }],
        resize: true
    });

    // Morris.Bar({
        // element: 'morris-bar-chart',
        // data: [{
            // y: '2012',
            // Reported: 3100,
            // Identified: 1190
        // }, {
            // y: '2013',
            // Reported: 3575,
            // Identified: 1565
        // }, {
            // y: '2014',
            // Reported: 3450,
            // Identified: 1040
        // }, {
            // y: '2015',
            // Reported: 1575,
            // Identified: 565
        // }, {
            // y: '2016',
            // Reported: 2450,
            // Identified: 840
        // }, {
            // y: '2017',
            // Reported: 2375,
            // Identified: 665
        // }, {
            // y: '2018',
            // Reported: 3783,
            // Identified: 820
        // }],
        // xkey: 'y',
        // ykeys: ['Reported', 'Identified'],
        // labels: ['Reported', 'Identified'],
        // hideHover: 'auto',
        // resize: true
    // });
    
});
