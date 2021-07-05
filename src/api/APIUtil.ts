import Axios from 'axios';

export class APIError extends Error {
    response: { message: ''; status: null };
    responseJSON: string;

    constructor(response: any) {
        super(`Server responded with ${response.statusText}: ${response.status} on URL: ${response.config.url}`);
        this.response = { status: response.status, ...response.data };
        this.responseJSON = JSON.stringify(response.data);
    }

    get responseError() {
        return JSON.parse(this.responseJSON);
    }
}

export interface ApiUtilArguments {
    onError: (error: APIError | Error) => void;
    onFetchStart: () => void;
    onFetchEnd: () => void;
}

export class APIUtil {
    onError: ApiUtilArguments['onError'];
    onFetchStart: ApiUtilArguments['onFetchStart'];
    onFetchEnd: ApiUtilArguments['onFetchEnd'];

    constructor(args: ApiUtilArguments) {
        this.onError = args.onError;
        this.onFetchStart = args.onFetchStart;
        this.onFetchEnd = args.onFetchEnd;
    }

    async request(url: string, method: string = 'GET', body: object | null = null) {
        let options = { headers: { 'Content-Type': 'application/json' } };

        if (body) {
            options = { ...options, ...body };
        }

        let response;
        try {
            this.onFetchStart();
            if (method === 'GET') {
                response = await Axios.get(url, options);
            } else if (method === 'POST') {
                response = await Axios.post(url, options);
            } else if (method === 'PUT') {
                response = await Axios.put(url, options);
            } else if (method === 'DELETE') {
                response = await Axios.delete(url, options);
            }
            return response?.data;
        } catch (e) {
            let error: APIError | Error;

            if (e.response && e.response.data) {
                error = new APIError(e.response);
            } else {
                error = new Error(e);
            }

            console.log(error);
            this.onError(error);
        } finally {
            this.onFetchEnd();
        }
    }
}
