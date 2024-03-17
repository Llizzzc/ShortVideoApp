<template>
  <view class="page">
    <!-- 这里是状态栏, 每个页面都需要有, 目的不让页面覆盖状态栏 -->
    <view :style="{ height: statusBarHeight + 'px' }"></view>
    <image
      class="icon-close"
      src="../../static/images/icon-close-black.png"
      @click="close"></image>
    <view class="login-box">
      <text class="code-text">验证码登录</text>
      <text class="code-tips">注: 未注册的手机号验证通过后将自动注册</text>
      <view class="mobile-box">
        <text class="mobile-prefix">+86</text>
        <view class="middle"></view>
        <input
          type="number"
          class="mobile"
          :value="mobile"
          :model="mobile"
          @input="typingMobile"
          placeholder="请输入手机号"
          maxlength="11" />
      </view>
      <view class="line"></view>
      <view class="code-box">
        <input
          type="number"
          class="code"
          :value="verifyCode"
          :model="verifyCode"
          @input="typingVerifyCode"
          placeholder="请输入验证码"
          maxlength="6" />
        <view class="middle"></view>
        <view
          class="code-btn"
          :class="{ 'btn-code': !codeTouched, 'btn-code-touched': codeTouched }"
          @touchstart="touchstartCode"
          @touchend="touchendCode"
          @click="getCode">
          <text class="code-btn-text">
            {{ codeBtnText }}
          </text>
        </view>
      </view>
      <view class="line"></view>
      <text class="login-password-btn-text" @click="usePassword">
        使用账号密码登陆
      </text>
      <view
        class="login-btn"
        :class="{
          'btn-login': !loginTouched,
          'btn-login-touched': loginTouched,
        }"
        @touchstart="touchstartLogin"
        @touchend="touchendLogin"
        @click="loginOrRegist">
        <text class="login-btn-text">登录 / 注册</text>
      </view>
      <view class="notice">
        <text>注意：</text>
        <text>
          1. 体验版启动后自动登录，退出后可以使用手机号进行登录或注册。
        </text>
        <text>
          2. 体验版的默认测试账号中包含一些内置数据，可用于测试或查看。
        </text>
        <text>3. 体验版不能发布视频。</text>
        <text>4. 体验版中留言和修改用户信息等操作会定时自动清除。</text>
      </view>
    </view>
  </view>
</template>

<script>
let system = uni.getSystemInfoSync();
let app = getApp();
export default {
  data() {
    return {
      statusBarHeight: 0,
      mobile: "",
      verifyCode: "",
      codeBtnText: "获得验证码",
      codeTimes: 0,
      loginTouched: false,
      codeTouched: false,
    };
  },
  onLoad() {
    this.statusBarHeight = system.statusBarHeight;
  },
  methods: {
    usePassword() {
      uni.navigateTo({
        url: "./loginWithPassword",
        animationType: "none",
      });
    },
    touchstartLogin() {
      this.loginTouched = true;
    },
    touchendLogin() {
      this.loginTouched = false;
    },
    touchstartCode() {
      // timer在运行期间, 禁止触摸
      if (this.codeTimes > 0) {
        return;
      } else {
        this.codeTouched = true;
      }
    },
    touchendCode() {
      // timer在运行期间, 禁止触摸
      if (this.codeTimes > 0) {
        return;
      } else {
        this.codeTouched = false;
      }
    },
    close() {
      uni.navigateBack({
        delta: 1,
        animationType: "slide-out-bottom",
      });
    },
    typingMobile(e) {
      let event = e;
      this.mobile = e.detail.value;
    },
    typingVerifyCode(e) {
      let event = e;
      this.verifyCode = e.detail.value;
    },
    getCode() {
      if (this.codeTimes > 0) {
        return;
      }
      this.codeTouched = true;
      // 手机号为空判断
      if (app.isStrEmpty(this.mobile)) {
        uni.showToast({
          title: "输入的手机号为空!",
          icon: "none",
        });
        this.codeTouched = false;
        return;
      }
      // 手机号长度验证
      if (this.mobile.length != 11) {
        uni.showToast({
          title: "请输入11位手机号!",
          icon: "none",
        });
        this.codeTouched = false;
        return;
      }
      let serverUrl = app.globalData.serverUrl;
      let me = this;
      // 调用后端发送验证码
      uni.request({
        method: "GET",
        url: serverUrl + "/passport/getSMSCode?mobile=" + this.mobile,
        success(result) {
          // 显示后端信息
          uni.showToast({
            title: result.data.msg,
            icon: "none",
          });
          // 开始倒数60秒限制
          if (me.codeTimes == 0) {
            me.doTimer(59);
          }
        },
      });
    },
    // 发送验证码的倒计时方法
    doTimer(times) {
      // 倒计时定时器
      let smsTimer = setInterval(() => {
        let left = times--;
        if (left <= 0) {
          this.codeTouched = false;
          this.codeBtnText = "发送验证码";
          clearInterval(smsTimer);
        } else {
          this.codeBtnText = left + "s";
        }
        this.codeTimes = left;
      }, 1000);
    },
    loginOrRegist() {
      // 手机号为空判断
      if (app.isStrEmpty(this.mobile)) {
        uni.showToast({
          title: "输入的手机号为空!",
          icon: "none",
        });
        return;
      }

      // 验证码判断空
      if (app.isStrEmpty(this.verifyCode)) {
        uni.showToast({
          title: "验证码为空!",
          icon: "none",
        });
        return;
      }

      if (this.mobile.length != 11) {
        uni.showToast({
          title: "请输入11位手机号!",
          icon: "none",
        });
        return;
      }

      // 验证码长度判断
      if (this.verifyCode.length != 6) {
        uni.showToast({
          title: "请输入6位数验证码!",
          icon: "none",
        });
        return;
      }

      let serverUrl = app.globalData.serverUrl;
      uni.showLoading({
        title: "登陆中...",
      });
      // 调用后端登录注册
      uni.request({
        method: "POST",
        url: serverUrl + "/passport/login",
        data: {
          mobile: this.mobile,
          smsCode: this.verifyCode,
        },
        success(result) {
          let status = result.data.status;
          if (status == 210) {
            uni.hideLoading();
            let userInfo = result.data.data;
            app.setUserInfoSession(userInfo);
            app.setUserSessionToken(userInfo.userToken);
            uni.showToast({
              title: result.data.msg,
              icon: "none",
              duration: 2000,
            });
            uni.reLaunch({
              url: "../me/me",
            });
          } else {
            uni.showToast({
              title: result.data.msg,
              icon: "none",
              duration: 3000,
            });
          }
        },
      });
    },
  },
};
</script>

<style lang="scss">
.page {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background-color: #ffffff;
  padding: 20rpx;
  display: flex;
  flex-direction: column;

  .icon-close {
    width: 50rpx;
    height: 50rpx;
  }

  .login-box {
    display: flex;
    flex-direction: column;
    margin-top: 200rpx;
    padding: 20rpx;
    .code-text {
      font-weight: 600;
      font-size: 60rpx;
    }
    .code-tips {
      font-weight: 300;
      font-size: 34rpx;
      color: #808080;
      margin-top: 20rpx;
    }
    .mobile-box,
    .code-box {
      height: 100rpx;
      display: flex;
      flex-direction: row;
      padding: 0 20rpx;
      border-color: #808080;
      border-width: 1px;
      .mobile-prefix {
        color: #808080;
        font-weight: 400;
        font-size: 36rpx;
        align-self: center;
      }
      .middle {
        align-self: center;
        width: 1rpx;
        background-color: #afafb3;
        height: 60rpx;
        margin: 0 40rpx;
      }
      .mobile {
        align-self: center;
        color: #0c0c0c;
        font-weight: 400;
        font-size: 38rpx;
      }
      .code {
        align-self: center;
        width: 360rpx;
        color: #0c0c0c;
        font-weight: 400;
        font-size: 38rpx;
      }
      .code-btn {
        border-width: 1px;
        border-color: #ffffff;
        width: 220rpx;
        height: 80rpx;
        border-radius: 60rpx;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-self: center;
        .code-btn-text {
          font-size: 30rpx;
          color: #ffffff;
          font-weight: 300;
          align-self: center;
        }
      }
      .btn-code {
        background-color: #0abb03;
      }
      .btn-code-touched {
        background-color: #6be866;
      }
    }

    .line {
      height: 1rpx;
      background-color: #afafb3;
    }

    .login-password-btn-text {
      align-self: center;
      margin-top: 20rpx;
      color: #808080;
    }

    .login-btn {
      margin-top: 20rpx;
      border-width: 1px;
      border-color: #ffffff;
      width: 650rpx;
      height: 120rpx;
      opacity: 0.8;
      border-radius: 100rpx;
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-self: center;
      .login-btn-text {
        font-size: 40rpx;
        color: #ffffff;
        font-weight: 500;
        align-self: center;
      }
    }

    .btn-login {
      background-color: #ef274d;
    }

    .btn-login-touched {
      background-color: #e27c82;
    }

    .notice {
      display: flex;
      flex-direction: column;
      margin-top: 50rpx;
      color: #808080;
      font-size: 30rpx;
    }
  }
}
</style>
