import {createBrowserHistory} from 'history'
import {unstable_HistoryRouter as HistoryRouter} from 'react-router-dom'

const history = createBrowserHistory()

export {
    HistoryRouter,//在App.js中代替<BrowserRouter><BrowserRouter/>
    history
}