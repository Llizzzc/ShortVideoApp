<script>
import provinceList from "./json/area_province.js";
import cityList from "./json/area_city.js";
import districtList from "./json/area_district.js";
export default {
  // 全局属性
  globalData: {
    serverUrl: "内网穿透",
    provinceList: provinceList,
    cityList: cityList,
    districtList: districtList,
  },

  onLaunch: function () {
    let me = this;
    uni.onTabBarMidButtonTap(() => {
      let myUserInfo = getApp().getUserInfoSession();

      // 未登录不能发布视频
      if (myUserInfo == null) {
        uni.showToast({
          duration: 3000,
          title: "请登录~",
          icon: "none",
        });
        uni.navigateTo({
          url: "../loginRegist/loginRegist",
          animationType: "slide-in-bottom",
          success() {
            me.loginWords = "请登录";
          },
        });
        return;
      }

      uni.chooseVideo({
        sourceType: ["album"],
        compressed: false,
        success(e) {
          uni.navigateTo({
            url: "/pages/publish/publish?fileObjectEvent=" + JSON.stringify(e),
          });
        },
      });
    });
  },
  onShow: function () {},
  onHide: function () {},
  methods: {
    graceNumber(number) {
      if (number == 0) {
        return "0";
      } else if (number > 999 && number <= 9999) {
        return (number / 1000).toFixed(1) + "k";
      } else if (number > 9999 && number <= 99999) {
        return (number / 10000).toFixed(1) + "w";
      } else if (number > 99999) {
        return "10w+";
      }
      return number;
    },
    // 时间格式化时间为: 多少分钟前、多少天前
    // time 2020-09-10 20:20:20
    getDateBeforeNow(stringTime) {
      stringTime = new Date(stringTime.replace(/-/g, "/"));
      let minute = 1000 * 60;
      let hour = minute * 60;
      let day = hour * 24;
      let week = day * 7;
      let month = day * 30;
      let time1 = new Date().getTime(); //当前的时间戳
      let time2 = Date.parse(new Date(stringTime)); //指定时间的时间戳
      let time = time1 - time2;
      let result = null;
      if (time < 0) {
        result = stringTime;
      } else if (time / month >= 1) {
        result = parseInt(time / month) + "月前";
      } else if (time / week >= 1) {
        result = parseInt(time / week) + "周前";
      } else if (time / day >= 1) {
        result = parseInt(time / day) + "天前";
      } else if (time / hour >= 1) {
        result = parseInt(time / hour) + "小时前";
      } else if (time / minute >= 1) {
        result = parseInt(time / minute) + "分钟前";
      } else {
        result = "刚刚";
      }
      return result;
    },

    // 判断是否为空
    isStrEmpty(str) {
      if (
        str == null ||
        str == undefined ||
        (typeof str == "object" && str == {}) ||
        (typeof str == "string" && str.trim() == "")
      ) {
        return true;
      } else {
        return false;
      }
    },

    // 判断用户是否登录
    userIsLogin() {
      let userToken = this.getUserSessionToken();
      let userInfo = this.getUserInfoSession();
      if (!this.isStrEmpty(userToken) && !this.isStrEmpty(userInfo)) {
        return true;
      } else {
        return false;
      }
    },

    // 用户登录以后的session存取, token和userInfo
    setUserSessionToken(token) {
      uni.setStorageSync("userToken", token);
    },
    getUserSessionToken() {
      let token = uni.getStorageSync("userToken");
      if (this.isStrEmpty(token)) {
        return "";
      }
      return token;
    },
    setUserInfoSession(user) {
      uni.setStorageSync("userInfo", JSON.stringify(user));
    },
    getUserInfoSession() {
      let user = uni.getStorageSync("userInfo");
      if (this.isStrEmpty(user)) {
        return null;
      }
      return JSON.parse(user);
    },

    // 清除用户信息缓存
    clearUserInfo() {
      uni.removeStorageSync("userInfo");
      uni.removeStorageSync("userToken");
    },

    // 日期格式化
    dateFormat(fmt, date) {
      let ret;
      const opt = {
        "Y+": date.getFullYear().toString(), // 年
        "m+": (date.getMonth() + 1).toString(), // 月
        "d+": date.getDate().toString(), // 日
        "H+": date.getHours().toString(), // 时
        "M+": date.getMinutes().toString(), // 分
        "S+": date.getSeconds().toString(), // 秒
      };
      for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
          fmt = fmt.replace(
            ret[1],
            ret[1].length == 1 ? opt[k] : opt[k].padStart(ret[1].length, "0")
          );
        }
      }
      return fmt;
    },

    // 获得星座
    getAstro(m, d) {
      return "魔羯水瓶双鱼白羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯".substr(
        m * 2 - (d < "102223444433".charAt(m - 1) - -19) * 2,
        2
      );
    },

    // 获得生肖
    getAnimal(year) {
      year = year % 12;
      let animal = "";
      switch (year) {
        case 1:
          animal = "鸡";
          break;
        case 2:
          animal = "狗";
          break;
        case 3:
          animal = "猪";
          break;
        case 4:
          animal = "鼠";
          break;
        case 5:
          animal = "牛";
          break;
        case 6:
          animal = "虎";
          break;
        case 7:
          animal = "兔";
          break;
        case 8:
          animal = "龙";
          break;
        case 9:
          animal = "蛇";
          break;
        case 10:
          animal = "马";
          break;
        case 11:
          animal = "羊";
          break;
        case 0:
          animal = "猴";
          break;
      }
      return animal;
    },
  },
};
</script>

<style>
/*每个页面公共css */
.activate-line {
  background-color: #ffffff;
}
</style>
