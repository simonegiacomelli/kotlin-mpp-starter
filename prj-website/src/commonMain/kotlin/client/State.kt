package client

import accesscontrol.Session

interface State : StateAbs {
    var sessionOrNull: Session?
}