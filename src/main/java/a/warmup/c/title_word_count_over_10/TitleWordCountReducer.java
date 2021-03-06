package a.warmup.c.title_word_count_over_10;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Outputs the number of titles which consist of more than 10 words
 */
public class TitleWordCountReducer extends Reducer< Text, IntWritable, IntWritable, NullWritable > {

    private int numberOfTitles;

    @Override
    protected void setup( Context context ) throws IOException, InterruptedException {

        super.setup( context );

        numberOfTitles = 0;
    }

    @Override
    protected void reduce( Text key, Iterable< IntWritable > values, Context context ) throws IOException, InterruptedException {

        for( IntWritable value : values ) {

            if( value.get() >= 10 ) {

                numberOfTitles++;
                break;
            }
        }
    }

    @Override
    protected void cleanup( Context context ) throws IOException, InterruptedException {

        context.write( new IntWritable( numberOfTitles ), NullWritable.get() );
    }
}

