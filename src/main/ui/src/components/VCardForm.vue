<script>
// noinspection ES6UnusedImports
import {ref} from "vue";
import axios from "axios";
import GoogleWalletButton from "@/components/GoogleWalletButton.vue";
import AppleWalletButton from "@/components/AppleWalletButton.vue";
import placeHolderQr from "../assets/qr-placeholder.svg"

export default {
  components: {AppleWalletButton, GoogleWalletButton},
  data() {
    return {
      vcard: undefined,
      qrCode: placeHolderQr,
      contactTypeSelects: [
        {label: 'Einzelperson', value: 'INDIVIDUAL'},
        {label: 'Organisation', value: 'ORGANISATION', attrs: {disabled: true}},
        {label: 'Gruppe', value: 'GROUP', attrs: {disabled: true}},
        {label: 'Team', value: 'TEAM', attrs: {disabled: true}}
      ],
      genderSelects: [
        {label: 'Keine Angabe', value: null},
        {label: 'Andere', value: 'OTHER'},
        {label: 'Intersex', value: 'INTERSEX'},
        {label: 'Männlich', value: 'MALE'},
        {label: 'Weiblich', value: 'FEMALE'}
      ],
      titleSelects: [
        {label: 'Keine Angabe', value: null},
        {label: 'Dr.', value: 'DR'},
        {label: 'Prof.', value: 'PROF'}
      ],
      mediaCategorySelects: [
        {label: 'Privat', value: 'HOME'},
        {label: 'Arbeit', value: 'WORK'}
      ]
    }
  },
  methods: {
    async sendForm(data) {
      let vcard = data.vcard
      let qrCode = await this.generateQr(vcard)
      if (qrCode !== undefined) {
        this.vcard = vcard
        this.qrCode = qrCode
      }
    },
    async generateQr(vcard) {
      await axios.post('/vcard/qr?size=250', vcard, {responseType: 'blob'})
          .then(response => {
            let imageAsBlob = new Blob([response.data], {type: "image/png"})
            return (window.URL || window.webkitURL).createObjectURL(imageAsBlob)
          })
          .catch(function (error) {
            console.log(error)
            console.log(vcard)
            alert(`${error.status} - Fehler beim generieren des QR-Codes`)
          })
    }
  }
}
</script>

<template>
  <FormKit id="vcard" #default="{ state: { valid } }" :actions="false" type="form" @submit="sendForm">
    <FormKit id="multistep-bar" :allow-incomplete="false" :hide-progress-labels="false" name="vcard"
             tab-style="progress"
             type="multi-step">
      <FormKit label="Visitenkarte" name="vcardType" type="step">
        <h3>Informationen zur Visitenkarte</h3>
        <FormKit :options="contactTypeSelects" label="Art der Visitenkarte" name="contactType" type="radio"
                 validation="required" validation-visibility="live"/>
      </FormKit>
      <FormKit label="Person" name="personalInformation" type="step">
        <h3>Angaben zur Person</h3>
        <div class="row">
          <FormKit :options="genderSelects" label="Gender" name="gender" outer-class="col-sm-6" type="select"/>
          <FormKit :options="titleSelects" label="Title" name="title" outer-class="col-sm-6" type="select"/>
        </div>
        <div class="row">
          <FormKit label="Vorname" name="firstname" outer-class="col-sm-6" validation="required"
                   validation-visibility="live"/>
          <FormKit label="Nachname" name="lastname" outer-class="col-sm-6" validation="required"
                   validation-visibility="live"/>
        </div>
        <div class="row">
          <FormKit help="Optional" label="Geburtsdatum" name="birthdate" outer-class="col-sm-4" type="date"/>
          <FormKit help="Optional" label="Geburtsort" name="birthplace" outer-class="col-sm-8"
                   placeholder="Berlin, Deutschland"/>
        </div>
      </FormKit>
      <FormKit label="Arbeit" name="workInformation" type="step">
        <h3>Angaben zum Beruf</h3>
        <div class="row">
          <FormKit help="Optional" label="Position" name="jobTitle"/>
        </div>
        <div class="row">
          <FormKit help="Optional" label="Gesellschaft" name="company" outer-class="col-sm-6"/>
          <FormKit help="Optional" label="Abteilung" name="department" outer-class="col-sm-6"/>
        </div>
        <div class="row">
          <FormKit help="Optional" label="Team" name="team" outer-class="col-sm-6"/>
          <FormKit help="Optional" label="Aufgabenbereich" name="role" outer-class="col-sm-6"/>
        </div>
      </FormKit>
      <FormKit label="Kontakt" name="contactInformation" type="step">
        <h3>Kontaktmöglichkeiten</h3>
        <FormKit :max="mediaCategorySelects.length" add-label="Hinzufügen" help="Est ist nur eine Telefonnummer pro Kategorie zulässig"
                 label="Telefonnummern" min="1" name="telephones"
                 type="repeater">
          <div class="row">
            <FormKit :options="mediaCategorySelects" label="Kategorie" name="category" outer-class="col-sm-4"
                     type="select" validation="required"/>
            <FormKit label="Telefonnummer" name="number" outer-class="col-sm-8" type="tel" validation="required"
                     prefixIcon="telephone" validation-visibility="live"/>
          </div>
        </FormKit>
        <FormKit :max="mediaCategorySelects.length" add-label="Hinzufügen" help="Est ist nur eine E-Mail pro Kategorie zulässig"
                 label="E-Mail Adressen" min="1" name="emails"
                 type="repeater">
          <div class="row">
            <FormKit :options="mediaCategorySelects" label="Kategorie" name="category" outer-class="col-sm-4"
                     type="select" validation="required"/>
            <FormKit label="E-Mail" name="email" outer-class="col-sm-8" type="email" validation="required|email"
                     validation-visibility="live"/>
          </div>
        </FormKit>
      </FormKit>
      <FormKit label="Internet" name="socialMedia" type="step">
        <h2>Erreichbarkeit im Internet</h2>
        <div class="row">
          <FormKit help="Optional" label="Website" name="website" placeholder="https://..." type="url"/>
        </div>
      </FormKit>
      <FormKit label="Speichern" name="qrCode" type="step">
        <FormKit :disabled="!valid" label="Generiere QR-Code" type="submit"/>
        <div class="row">
          <div class="col-sm-6">
            <img id="vcard-qr" :src="this.qrCode" alt="Dein QR-Code" title="Dein QR-Code"/>
          </div>
          <div class="col-sm-6">
            <div class="row">
              <GoogleWalletButton :qr-code="this.qrCode" :vcard="this.vcard"/>
            </div>
            <div class="row">
              <AppleWalletButton :qr-code="this.qrCode" :vcard="this.vcard"/>
            </div>
          </div>
        </div>
      </FormKit>
    </FormKit>
  </FormKit>
</template>

<style>
/*noinspection CssUnusedSymbol*/
#multistep-bar .formkit-wrapper {
  max-width: 100%;
}

#vcard-qr {
  width: 250px;
}
</style>