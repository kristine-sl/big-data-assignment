package b.discover.f.topic;

import _other.helpers.MapSorter;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TopicReducer extends Reducer< Text, IntWritable, Text, IntWritable > {

    Map< String, Integer > words;

    @Override
    protected void setup( Context context ) throws IOException, InterruptedException {

        words = new HashMap< String, Integer >();
    }

    @Override
    protected void reduce( Text key, Iterable< IntWritable > values, Context context ) throws IOException, InterruptedException {

        for ( IntWritable value : values ) {

            String word = key.toString();

            if ( words.containsKey( word ) ) words.put( word, words.get( word ) + value.get() );

            else words.put( word, value.get() );
        }
    }

    @Override
    protected void cleanup( Context context ) throws IOException, InterruptedException {

        for ( int i = 0; i < Topic.NUMBER_OF_TOPICS; i++ ) {

            String word = MapSorter.getHighestValue( words );

            context.write( new Text( word ), new IntWritable( words.get( word ) ) );

            words.remove( word );
        }
    }
}