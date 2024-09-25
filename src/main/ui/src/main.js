import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap"
import './assets/main.css'

import {createApp} from 'vue'
import App from './App.vue'
import axios from "axios";
import VueAxios from "vue-axios";
import {plugin as FormKit} from "@formkit/vue";
import customConfig from "../formkit.config.js"

const app = createApp(App)
app.use(VueAxios, axios)
app.use(FormKit, customConfig)
app.mount('#app')