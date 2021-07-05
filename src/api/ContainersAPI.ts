import Axios from 'axios';
import { LOAD_PLAN_URL } from '../constants';
import { APIUtil, ApiUtilArguments } from './APIUtil';

export class ContainersApi extends APIUtil {
    constructor(args: ApiUtilArguments) {
        super(args);
    }

    async fetchLoadPlan(jsonFile) {
        let data;

        try {
            // Call the endpoint like this(without passing options) otherwise the returned data is incomplete.
            data = await Axios.post(LOAD_PLAN_URL, {...jsonFile});;
        } catch (e) {
            throw e;
        }

        return data;
    }
}
