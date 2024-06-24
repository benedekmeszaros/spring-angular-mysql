import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-sidebar-nav',
  templateUrl: './sidebar-nav.component.html',
  styleUrl: './sidebar-nav.component.css'
})
export class SidebarNavComponent {
  @Input()
  public link: string;
  @Input()
  public icon: string;
  @Input()
  public size: string = '25';
  @Input()
  public style: string = 'selected';
}
