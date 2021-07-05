import Axios from 'axios';
import { ConvertedXlsDto } from 'services/generate-json/generate-json.types';
import { LOAD_PLAN_URL } from '../constants';
import { APIUtil, ApiUtilArguments } from './APIUtil';

export class ContainersApi extends APIUtil {
    constructor(args: ApiUtilArguments) {
        super(args);
    }

    async getLoadPlan(jsonFile: ConvertedXlsDto) {
        let data;

        try {
            // TODO vasko - maybe refactor later to use APIUtil methods.
            // Call the endpoint like this(without passing options) otherwise the returned data is incomplete.
            data = await Axios.post(LOAD_PLAN_URL, { ...jsonFile }, { responseType: 'blob' });
        } catch (e) {
            throw e;
        }

        return data;
    }
}
