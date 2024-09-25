import {defaultConfig} from "@formkit/vue";
import {de} from '@formkit/i18n'
import {genesisIcons} from '@formkit/icons'
import '@formkit/themes/genesis'
import {createMultiStepPlugin} from '@formkit/addons'
import '@formkit/addons/css/multistep'
import {createProPlugin, inputs} from '@formkit/pro'
import '@formkit/pro/genesis'

const config = defaultConfig({
    locales: {de},
    locale: 'de',
    theme: 'genesis',
    icons: genesisIcons,
    plugins: [createMultiStepPlugin(), createProPlugin('fk-c6706d59de', inputs)]
})

export default config