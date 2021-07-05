import { LOAD_PLAN_URL } from '../constants';
import { APIUtil, ApiUtilArguments } from './APIUtil';

export class ContainersApi extends APIUtil {
    constructor(args: ApiUtilArguments) {
        super(args);
    }

    async fetchLoadPlan(jsonFile) {
        let data;

        try {
            data = await this.request(LOAD_PLAN_URL, 'POST', {data: jsonFile});
        } catch (e) {
            throw e;
        }

        return data;
    }
}
