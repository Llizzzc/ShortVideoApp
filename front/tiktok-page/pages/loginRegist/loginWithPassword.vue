<template>
  <view class="page">
    <!-- 这里是状态栏, 每个页面都需要有, 目的不让页面覆盖状态栏 -->
    <view :style="{ height: statusBarHeight + 'px' }"></view>
    <image
      class="icon-close"
      src="../../static/images/icon-close-black.png"
      @click="close"></image>
    <view class="login-box">
      <text class="code-text">账号密码登录</text>
      <text class="code-tips">注: 未注册的账号请使用验证码登陆</text>
      <view class="account-box">
        <text class="account-text">账号</text>
        <view class="middle"></view>
        <input
          type="text"
          class="account"
          :value="account"
          :model="account"
          @input="typingAccount"
          placeholder="请输入账号"
          maxlength="16" />
      </view>
      <view class="line"></view>
      <view class="password-box">
        <text class="password-text">密码</text>
        <view class="middle"></view>
        <input
          type="password"
          class="password"
          :value="password"
          :model="password"
          @input="typingPassword"
          placeholder="请输入密码"
          maxlength="16" />
      </view>
      <view class="line"></view>
      <view
        class="login-btn"
        :class="{
          'btn-login': !loginTouched,
          'btn-login-touched': loginTouched,
        }"
        @touchstart="touchstartLogin"
        @touchend="touchendLogin"
        @click="loginWithPassword">
        <text class="login-btn-text">登录</text>
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
      account: "",
      password: "",
      loginTouched: false,
    };
  },
  onLoad() {
    this.statusBarHeight = system.statusBarHeight;
  },
  methods: {
    touchstartLogin() {
      this.loginTouched = true;
    },
    touchendLogin() {
      this.loginTouched = false;
    },
    close() {
      uni.navigateBack({
        delta: 1,
        animationType: "none",
      });
    },
    typingAccount(e) {
      let event = e;
      this.account = e.detail.value;
    },
    typingPassword(e) {
      let event = e;
      this.password = e.detail.value;
    },

    loginWithPassword() {
      // 账号判空
      if (app.isStrEmpty(this.account)) {
        uni.showToast({
          title: "输入的账号为空!",
          icon: "none",
        });
        return;
      }
      // 密码判空
      if (app.isStrEmpty(this.password)) {
        uni.showToast({
          title: "输入的密码为空!",
          icon: "none",
        });
        return;
      }
      // 账号长度判断
      if (this.account.length < 5) {
        uni.showToast({
          title: "请输入5~16位的账号!",
          icon: "none",
        });
        return;
      }
      // 密码长度判断
      if (this.password.length < 5) {
        uni.showToast({
          title: "请输入5~16位的密码!",
          icon: "none",
        });
        return;
      }
      let serverUrl = app.globalData.serverUrl;
      uni.showLoading({
        title: "登陆中...",
      });
      uni.request({
        method: "POST",
        url: serverUrl + "/passport/loginWithPassword",
        data: {
          account: this.account,
          password: this.password,
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
    .account-box,
    .password-box {
      height: 100rpx;
      display: flex;
      flex-direction: row;
      padding: 0 20rpx;
      border-color: #808080;
      border-width: 1px;
      .account-text,
      .password-text {
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
      .account {
        align-self: center;
        color: #0c0c0c;
        font-weight: 400;
        font-size: 38rpx;
      }
      .password {
        align-self: center;
        width: 360rpx;
        color: #0c0c0c;
        font-weight: 400;
        font-size: 38rpx;
      }
    }

    .line {
      height: 1rpx;
      background-color: #afafb3;
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
