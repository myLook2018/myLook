import { Directive, HostListener, Output, EventEmitter } from '@angular/core';

@Directive({
  selector: '[appDropZone]'
})
export class DropZoneDirective {
   @Output() dropped = new EventEmitter<FileList>();
   @Output() hovered = new EventEmitter<boolean>();



  constructor() { }

  @HostListener('drop', ['$event'])
  ondrop($event) {
    $event.prenvetDefault();
    this.dropped.emit($event.dataTransfer.files);
    this.hovered.emit(true);
  }


  @HostListener('dragover', ['$event'])
  ondragleave($event) {
    $event.prenvetDefault();
    this.hovered.emit(false);
  }








}
