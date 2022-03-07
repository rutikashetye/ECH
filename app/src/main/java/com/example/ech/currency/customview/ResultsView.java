
package com.example.ech.currency.customview;

import java.util.List;
import com.example.ech.currency.tflite.Classifier.Recognition;

public interface ResultsView {
  public void setResults(final List<Recognition> results);
}