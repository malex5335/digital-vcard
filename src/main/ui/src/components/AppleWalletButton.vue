<script>
// noinspection ES6UnusedImports
import {ref} from "vue";
import axios from "axios";

export default {
  props: {
    qrCode: undefined,
    vcard: undefined
  },
  methods: {
    async addToAppleWallet() {
      await axios.post('/apple/file', {qrCode: this.qrCode, data: this.vcard}, {responseType: 'blob'})
          .then(response => {
            return response
          })
          .catch(function (error) {
            console.log(error)
            alert(`${error.status} - Fehler beim generieren des Apple-Files`)
          })
    }
  }
}
</script>

<template>
  <FormKit :disabled="qrCode === undefined || vcard === undefined" label="Zum Apple-Wallet hinzufügen" prefix-icon="apple"
           type="button" @click="addToAppleWallet"/>
</template>