<script>
// noinspection ES6UnusedImports
import {ref} from "vue";
import axios from "axios";

export default {
  props: {
    vcard: undefined
  },
  methods: {
    async addToGoogleWallet() {
      await axios.post('/google/token', this.vcard)
          .then(response => {
            window.location.href = `https://pay.google.com/gp/v/save/${response.data.token}`
          })
          .catch(function (error) {
            console.log(error)
            alert(`${error.status} - Fehler beim generieren des Google-Tokens`)
          })
    }
  }
}
</script>

<template>
  <FormKit :disabled="vcard === undefined" label="Zum GoogleWallet hinzufügen" prefix-icon="google" type="button"
           @click="addToGoogleWallet"/>
</template>