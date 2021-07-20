import { Action } from '@ngrx/store';
import { ILoadPlan } from './loadPlan';

export const ADD_LOAD_PLAN = '[Load Plan] Add';
export const CLEAR_LOAD_PLAN = '[Load Plan] Clear';
export const CLEAR_STATE = '[App] Clear global state';

export class AddLoadPlan implements Action {
    readonly type = ADD_LOAD_PLAN;
  constructor(public payload: ILoadPlan) {
  }
    
}

export class RemoveLoadPlan implements Action {
    readonly type = CLEAR_LOAD_PLAN;
}

export class ClearGlobalState implements Action {
    readonly type = CLEAR_STATE;
}

export type loadPlanActions = AddLoadPlan | RemoveLoadPlan | ClearGlobalState;