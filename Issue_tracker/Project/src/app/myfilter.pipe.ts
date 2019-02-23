import { Pipe, PipeTransform } from '@angular/core';
@Pipe({
  name: 'issueFilter'
})

/* This class is responsible for filtering the item from the given array */
export class MyFilter implements PipeTransform {

  transform(items: any[], value: string, label:string): any[] {
    if (!items) return [];
    if (!value) return  items;
    
    if (value == '' || value == null) return [];
    return items.filter(e => e[label].indexOf(value) > -1 );
    
  }
 
}