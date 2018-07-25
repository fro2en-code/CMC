/**
 例子：
// 将当前时间换成时间格式字符串
var timestamp = 1515467945000;
var newDate = new Date();
newDate.setTime(timestamp);
newDate.format('yyyy-MM-dd hh:mm:ss') //打印：
 */
Date.prototype.format = function(format) {
    var date = {
       "M+": this.getMonth() + 1,
       "d+": this.getDate(),
       "h+": this.getHours(),
       "m+": this.getMinutes(),
       "s+": this.getSeconds(),
       "q+": Math.floor((this.getMonth() + 3) / 3),
       "S+": this.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
       format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
       if (new RegExp("(" + k + ")").test(format)) {
           format = format.replace(RegExp.$1, RegExp.$1.length == 1
              ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
       }
    }
    return format;
}

var DateUtil = (function(){
	/**
	 * 将当前时间换成时间格式字符串
	 * timestamp 数字日期，精确到秒，例子：1515467945000
	 */
	function timeToYYYY_MM_dd_hh_mm_ss(timestamp){
            var newDate = new Date();
            newDate.setTime(timestamp);
            return newDate.format('yyyy-MM-dd hh:mm:ss');
	}
    /**
     * 将当前时间换成时间格式字符串
     * timestamp 数字日期，精确到秒，例子：1515467945000
     */
    function timeToYYYY_MM_dd(timestamp){
        var newDate = new Date();
        newDate.setTime(timestamp);
        return newDate.format('yyyy-MM-dd');
    }

    /**
     * 获取今天零时的日期和时间，例子：2018-01-18 00:00:00
     */
    function todayYYYY_MM_dd_00_00_00(){
        return new Date().format('yyyy-MM-dd 00:00:00');
    }

    /**
     * 获取今天的日期和时间，例子：2018-01-18 10:15:20
     */
    function todayYYYY_MM_dd_hh_mm_ss(){
        return new Date().format('yyyy-MM-dd hh:mm:ss');
    }
    function todayMillisecond(){
        return new Date().getTime();
    }
    
    /**
     * 获取昨天的日期和时间，时间一定是00:00:00，例子：2018-01-18 00:00:00
     */
    function yesterdayYYYY_MM_dd_hh_mm_ss(){
        return timeToYYYY_MM_dd(new Date().getTime() - 1000*60*60*24) + ' 00:00:00';
    }
    
	return {
		todayMillisecond : todayMillisecond,
        timeToYYYY_MM_dd_hh_mm_ss : timeToYYYY_MM_dd_hh_mm_ss,
        timeToYYYY_MM_dd : timeToYYYY_MM_dd,
		todayYYYY_MM_dd_00_00_00 : todayYYYY_MM_dd_00_00_00,
        todayYYYY_MM_dd_hh_mm_ss : todayYYYY_MM_dd_hh_mm_ss,
        yesterdayYYYY_MM_dd_hh_mm_ss : yesterdayYYYY_MM_dd_hh_mm_ss
	};
})();