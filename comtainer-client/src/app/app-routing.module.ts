import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './modules/helpers/auth-guard';
import { HomeComponent } from './modules/home/home/home.component';
import { LoginComponent } from './modules/login/login.component';


const routes: Routes = [
  // { path: 'home', component: HomeComponent }

  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: '**', component: LoginComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }