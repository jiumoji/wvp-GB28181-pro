<template>
<div id="recordDownload" >
  <el-dialog :title="title" v-if="showDialog"  width="45rem" :append-to-body="true" :close-on-click-modal="false" :visible.sync="showDialog" :destroy-on-close="true" @close="close()" center>
    <el-row>
      <el-col :span="18" style="padding-top: 7px;">
        <el-progress :percentage="percentage"></el-progress>
      </el-col>
      <el-col :span="6" >
<!--       <el-dropdown size="mini" title="播放倍速" style="margin-left: 1px;" @command="gbScale">-->
<!--         <el-button-group>-->
<!--           <el-button size="mini" style="width: 100%">-->
<!--             {{scale}}倍速 <i class="el-icon-arrow-down el-icon&#45;&#45;right"></i>-->
<!--           </el-button>-->
<!--         </el-button-group>-->
<!--        <el-dropdown-menu  slot="dropdown">-->
<!--          <el-dropdown-item command="1">1倍速</el-dropdown-item>-->
<!--          <el-dropdown-item command="2">2倍速</el-dropdown-item>-->
<!--          <el-dropdown-item command="4">4倍速</el-dropdown-item>-->
<!--        </el-dropdown-menu>-->
<!--      </el-dropdown>-->
        <el-button icon="el-icon-download" v-if="percentage < 100" size="mini" title="点击下载可将以缓存部分下载到本地" @click="download()">停止缓存并下载</el-button>
      </el-col>
    </el-row>
  </el-dialog>
</div>
</template>


<script>

import moment from "moment";

export default {
    name: 'recordDownload',
    created() {


    },
    data() {
        return {
          title: "四倍速下载中...",
          deviceId: "",
          channelId: "",
          app: "",
          stream: "",
          mediaServerId: "",
          showDialog: false,
          scale: 1,
          percentage: 0.00,
          streamInfo: null,
          taskId: null,
          getProgressRun: false,
          getProgressForFileRun: false,

        };
    },
    methods: {
        openDialog: function (deviceId, channelId, app, stream, mediaServerId) {
            this.deviceId = deviceId;
            this.channelId = channelId;
            this.app = app;
            this.stream = stream;
            this.mediaServerId = mediaServerId;
            this.showDialog = true;
            this.getProgressRun = true;
            this.percentage = 0.0;
            this.getProgressTimer()
        },
        getProgressTimer(){
          if (!this.getProgressRun) {
            return;
          }
          if (this.percentage == 100 ) {
            this.getFileDownload();
            return;
          }
          setTimeout( ()=>{
            if (!this.showDialog) return;
            this.getProgress(this.getProgressTimer())
          }, 5000)
        },
        getProgress: function (callback){
          this.$axios({
            method: 'get',
            url: `/api/gb_record/download/progress/${this.deviceId}/${this.channelId}/${this.stream}`
          }).then((res)=> {
              console.log(res)
              console.log(res.data.progress)
              this.streamInfo = res.data;
              if (parseFloat(res.data.progress) == 1) {
                this.percentage = 100;
              }else {
                this.percentage = (res.data.progress*100).toFixed(1);
              }
              if (callback)callback();
          }).catch((e) =>{

          });
        },
        close: function (){
          if (this.streamInfo.progress < 100) {
            this.stopDownloadRecord();
          }
          this.showDialog=false;
          this.getProgressRun = false;
          this.getProgressForFileRun = false;
        },
        gbScale: function (scale){
          this.scale = scale;
        },
        download: function (){
          this.getProgressRun = false;
          if (this.streamInfo != null ) {
            if (this.streamInfo.progress < 1) {
              // 发送停止缓存
              this.stopDownloadRecord((res)=>{
                  this.getFileDownload()
              })
            }else {
              this.getFileDownload()
            }
          }
        },
        stopDownloadRecord: function (callback) {
          this.$axios({
            method: 'get',
            url: '/api/gb_record/download/stop/' + this.deviceId + "/" + this.channelId+ "/" + this.stream
          }).then((res)=> {
            if (callback) callback(res)
          });
        },
        getFileDownload: function (){
          this.$axios({
            method: 'get',
            url:`/record_proxy/${this.mediaServerId}/api/record/file/download/task/add`,
            params: {
              app: this.app,
              stream: this.stream,
              startTime: null,
              endTime: null,
            }
          }).then((res) =>{
            if (res.data.code === 0 && res.data.msg === "success") {
              // 查询进度
              this.title = "录像文件处理中..."
              this.taskId = res.data.data;
              this.percentage = 0.0;
              this.getProgressForFileRun = true;
              this.getProgressForFileTimer();
            }
          }).catch(function (error) {
            console.log(error);
          });
        },
        getProgressForFileTimer: function (){
          if (!this.getProgressForFileRun || this.percentage == 100) {
            return;
          }
          setTimeout( ()=>{
            if (!this.showDialog) return;
            this.getProgressForFile(this.getProgressForFileTimer())
          }, 1000)
        },
        getProgressForFile: function (callback){
          this.$axios({
            method: 'get',
            url:`/record_proxy/${this.mediaServerId}/api/record/file/download/task/list`,
            params: {
              app: this.app,
              stream: this.stream,
              taskId: this.taskId,
              isEnd: true,
            }
          }).then((res) => {
            console.log(res)
            if (res.data.code == 0) {
                this.percentage = parseFloat(res.data.data.percentage)*100
                 if (res.data.data[0].percentage === '1') {
                   this.getProgressForFileRun = false;
                   window.open(res.data.data[0].downloadFile)
                   this.close();
                 }else {
                   if (callback)callback()
                 }
            }
          }).catch(function (error) {
            console.log(error);
          });
        }
    }
};
</script>

<style>

</style>
