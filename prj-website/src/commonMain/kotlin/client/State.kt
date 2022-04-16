package client

import api.names.ApiAcSession

interface State : ClientState {
    var sessionOrNull: ApiAcSession?
}