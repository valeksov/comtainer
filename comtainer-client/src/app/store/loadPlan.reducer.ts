import { ILoadPlan } from './loadPlan';
import * as LoadActions from './loadPlan.actions';

const initialState: ILoadPlan = {
  containers: [],
  config: {},
  groups: [],
};

const newState = (state: any, newData: any) => {
  return Object.assign({}, state, newData);
};

export function loadPlanReducer<initialState>(
  state = initialState,
  action: LoadActions.loadPlanActions
) {
  console.log(action.type, state);
  
  switch (action.type) {
    case LoadActions.ADD_LOAD_PLAN:
      return newState(state, action.payload);
    case LoadActions.CLEAR_STATE:
      return newState(state, initialState);
    default:
      return state;
  }
 
}
