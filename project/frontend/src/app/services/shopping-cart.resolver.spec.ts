import { TestBed } from '@angular/core/testing';

import { ShoppingCartResolver } from './shopping-cart.resolver';

describe('ShoppingCartResolver', () => {
  let resolver: ShoppingCartResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    resolver = TestBed.inject(ShoppingCartResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
