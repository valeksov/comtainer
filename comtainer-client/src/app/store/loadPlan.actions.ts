import { Action } from '@ngrx/store';
import { ILoadPlan } from './loadPlan';

export const ADD_LOAD_PLAN = '[Load Plan] Add';
export const CLEAR_LOAD_PLAN = '[Load Plan] Clear';

export class AddLoadPlan implements Action {
    readonly type = ADD_LOAD_PLAN;
  constructor(public payload: ILoadPlan) {
  }
    
}

export class RemoveLoadPlan implements Action {
    readonly type = CLEAR_LOAD_PLAN;
}

export type loadPlanActions = AddLoadPlan | RemoveLoadPlan;