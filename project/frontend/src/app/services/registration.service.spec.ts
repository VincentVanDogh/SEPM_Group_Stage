import {TestBed} from '@angular/core/testing';

import {RegistrationService} from './registration.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {ReactiveFormsModule} from '@angular/forms';

describe('RegistrationService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, RouterTestingModule, ReactiveFormsModule],
  }));

  it('should be created', () => {
    const service: RegistrationService = TestBed.inject(RegistrationService);
    expect(service).toBeTruthy();
  });
});
